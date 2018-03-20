package actors
import java.io.File
import java.net.InetAddress
import java.nio.file.{Path, Paths}
import java.security.spec.ECPoint
import java.util

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Scheduler}
import akka.actor.Actor.Receive
import bitcoin.WalletMaker
import com.google.inject.Inject
import com.google.inject.name.Named
import forms.CreateWalletForm
import loggers.BitSnailLogger
import messages.{BitcoinTransactionReceived, InitiateBlockChain, LoadAllWallets, NotificationEmailSent}
import model.{TransactionStorage, WalletStorage}
import model.models.{BitcoinTransaction, SnailWallet}
import org.bitcoinj.core.listeners.PeerDataEventListener
import org.bitcoinj.core._
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.{RegTestParams, TestNet3Params}
import org.bitcoinj.store.{H2FullPrunedBlockStore, MemoryBlockStore}
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener
import org.spongycastle.util.encoders.Hex
import play.Configuration
import reactivemongo.play.json.collection.JSONCollection
import play.api.libs.json._
import play.modules.reactivemongo._

import scala.collection.JavaConversions._
import scala.concurrent.{duration, ExecutionContext}
import scala.concurrent.duration.FiniteDuration
@Named("BitcoinClientActor")
class BitcoinClientActor @Inject()(
    mongoApi: ReactiveMongoApi,
    config: Configuration,
    walletMaker: WalletMaker,
    system: ActorSystem,
    transactions: TransactionStorage,
    walletStorage: WalletStorage,
    bitcoinLogger: BitSnailLogger,
    @Named("NotificationSendingActor") notificationSendingActor: ActorRef
)(implicit ec: ExecutionContext)
    extends Actor {

  val bitcoinNetwork = config.getString("bitsnail.bitcoin.network")
  val networkParams = bitcoinNetwork match {
    case "regtest" => RegTestParams.get()
    case "testnet" => TestNet3Params.get()
  }
  val peerGroupContext = new Context(networkParams)
  val tablePath = Paths.get(".").resolve("BlockChainDB").normalize().toAbsolutePath.toString
  val blockStore = bitcoinNetwork match {
    case "regtest" => new MemoryBlockStore(networkParams)
    case "testnet" => new H2FullPrunedBlockStore(networkParams, tablePath, 1000)
  }
  val blockChain = new BlockChain(peerGroupContext, blockStore)
  var peerGroup: PeerGroup = new PeerGroup(peerGroupContext, blockChain)

  val walletListener = new WalletCoinsReceivedEventListener {
    override def onCoinsReceived(wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin): Unit = {
      val transactionId = tx.getHash.toString
      for {
        transaction <- transactions.findTransactionByTransactionId(transactionId)
      } yield {
        transaction match {
          case Some(t) =>
            bitcoinLogger.SeenTransaction(transactionId)

          case None =>
            // Find the matching wallet
            for {
              walletContext <- walletStorage.findWallet(Hex.toHexString(wallet.getImportedKeys.get(0).getPubKey))
            } yield {
              walletContext match {
                case Some(t) =>
                  for {
                    result <- transactions.insertTransaction(
                      BitcoinTransaction(t.publicKey,
                                         transactionId,
                                         BitcoinTransaction.NotSent,
                                         BitcoinTransaction.NotSent)
                    )
                  } yield {
                    if (result.ok) {
                      bitcoinLogger.NewTransaction(t, transactionId)
                      notificationSendingActor ! BitcoinTransactionReceived(t.transData,
                                                                            t.publicKeyAddress,
                                                                            transactionId,
                                                                            prevBalance,
                                                                            newBalance)
                    }
                  }
                case None =>
                  bitcoinLogger.MissingWallet(wallet)
              }
            }
        }
      }
    }
  }

  val blockChainDownloadListener = new PeerDataEventListener {
    override def getData(peer: Peer, m: GetDataMessage): util.List[Message] = null

    override def onChainDownloadStarted(peer: Peer, blocksLeft: Int): Unit = {
      bitcoinLogger.BlockChainDownloadStarted(blocksLeft)
    }

    override def onPreMessageReceived(peer: Peer, m: Message): Message = m

    override def onBlocksDownloaded(peer: Peer, block: Block, filteredBlock: FilteredBlock, blocksLeft: Int): Unit = {
      if (blocksLeft == 0) bitcoinLogger.FinishedBlockDownload()
    }
  }

  def addWallet(wallet: SnailWallet): Unit = {
    val jWallet = Wallet.fromKeys(networkParams, Seq(ECKey.fromPublicOnly(Hex.decode(wallet.publicKey))))
    jWallet.setDescription(wallet.transData.recipientEmail)
    jWallet.addCoinsReceivedEventListener(walletListener)
    jWallet.setAcceptRiskyTransactions(true)
    peerGroup.addWallet(jWallet)
    bitcoinLogger.StartedWatchingWallet(wallet)
  }

  override def receive: Receive = {
    case wallet: model.models.SnailWallet =>
      Context.propagate(peerGroupContext)
      walletStorage.insertWallet(wallet)
      addWallet(wallet)

    case init: InitiateBlockChain =>
      Context.propagate(peerGroupContext)
      peerGroup.setUserAgent("Bitcoin Mail Snail", "0.0")
      peerGroup.setStallThreshold(10000, 1)
      peerGroup.setMaxConnections(30)
      bitcoinNetwork match {
        case "regtest" =>
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4001))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4002))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4003))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4004))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4005))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4006))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4007))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4008))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4009))
          peerGroup.addAddress(new PeerAddress(InetAddress.getLoopbackAddress, 4010))
          peerGroup.start()
          bitcoinLogger.StartingBitcoinNetwork("regtest")
        case "testnet" =>
          peerGroup.addPeerDiscovery(new DnsDiscovery(networkParams))
          peerGroup.start()
          bitcoinLogger.StartingBitcoinNetwork("testnet")
      }

    case previousWallets: LoadAllWallets =>
      bitcoinLogger.LoadingAllWallets(previousWallets)
      Context.propagate(peerGroupContext)
      for {
        w <- previousWallets.wallets
      } yield addWallet(w)
      peerGroup.startBlockChainDownload(blockChainDownloadListener)

      system.scheduler.scheduleOnce(new FiniteDuration(5, duration.SECONDS)) {
        bitcoinLogger.KickingWalletWatcher()
        val w = CreateWalletForm.Data("gifted.primate@protonmail.com",
                                      Some("doohickeymastermind@protonmail.com"),
                                      "Here's your money!",
                                      remainAnonymous = false)
        val wallet = walletMaker(w)
        self ! wallet
      }

    case emailSent: NotificationEmailSent =>
      for {
        t <- transactions.findTransactionByTransactionId(emailSent.transactionId)
      } yield {
        t match {
          case Some(transaction) =>
            for {
              updateResult <- transactions.update(
                transaction.copy(
                  recipientState = if (emailSent.recipientSent) BitcoinTransaction.Sent else BitcoinTransaction.NotSent,
                  senderState = if (emailSent.senderSent) BitcoinTransaction.Sent else BitcoinTransaction.NotSent
                )
              )
            } yield {
              if (!updateResult.ok)
                bitcoinLogger.UnableToUpdateTransation(emailSent.transactionId,
                                                       updateResult.errmsg.getOrElse("Missing error message"))
            }

          case _ =>
            bitcoinLogger.MissingTransaction(emailSent.transactionId)
        }
      }
  }

}
