package bitcoin

import actors.messages.{InitiateBlockChain, LoadAllWallets}
import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import forms.CreateWalletForm
import model.WalletStorage
import model.models.SnailWallet
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext

class StartupWalletLoader @Inject()(
    mongoApi: ReactiveMongoApi,
    walletStorage: WalletStorage,
    @Named("BitcoinClientActor") bitcoinClient: ActorRef
)(implicit ec: ExecutionContext) {
  def initiateBlockChain =
    bitcoinClient ! InitiateBlockChain()

  def loadAllWallets =
    for {
      snails <- walletStorage.findAllWallets
    } yield {
      bitcoinClient ! LoadAllWallets(snails)
    }

  initiateBlockChain
  loadAllWallets

}
