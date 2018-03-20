package bitcoin

import java.security.SecureRandom

import play.mvc.Http
import fr.acinq.bitcoin._
import fr.acinq.bitcoin.Crypto._
import MnemonicCode._
import com.google.inject.Inject
import forms.CreateWalletForm
import model.models.{Seed, SnailWallet}
import play.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}
import javax.xml.bind.DatatypeConverter
import scala.util.Random

/*
 * Bitcoin is the master class for all blockchain interactions. Esentially this class contains all utilities for childs
 * @function random {Random}
 * @function hexStringGen {String} generates hex
 * @function binaryGen {BinaryData} converts hex to BinaryData
 * @function privateKeyGen {PrivateKey}
 * @function compressPrivateKey {PrivateKey}
 * @function publicKeyUncompressed {PublicKey}
 * @function pubkeyAddress {String}
 * @function publicKeyCompressed {PublicKey}
 * */

object WalletMaker {
  def random = new Random(new SecureRandom())

  def hexStringGen = {
    val bytes = Array.fill[Byte](32)(0)
    random.nextBytes(bytes)
    DatatypeConverter.printHexBinary(bytes)
  }

  def binaryGen(hex: String): BinaryData = BinaryData(hex)

  def privateKeyGen(key: BinaryData): PrivateKey = PrivateKey(key, compressed = false)

  def compressPrivateKey(key: BinaryData) = PrivateKey(key, compressed = true)

  def publicKeyUncompressed(priv: PrivateKey): PublicKey = priv.publicKey

  def pubKeyUncompressed(pubKey: PublicKey, prefix: Byte): String = Base58Check.encode(prefix, pubKey.hash160)

  def secretKey(priv: PrivateKey, prefix: Byte) = Base58Check.encode(prefix, priv.toBin)

  def publicKeyCompressed(priv: PrivateKey): PublicKey = priv.publicKey.copy(compressed = true)
}

class WalletMaker @Inject()(config: Configuration) {
  val bitcoinNetwork = config.getString("bitsnail.bitcoin.network")

  def genMnemonic(hexString: String): List[String] = toMnemonics(fromHexString(hexString))

  def genSeed(mnemonics: Seq[String]): BinaryData = toSeed(mnemonics, "TREZOR")

  def apply(transData: CreateWalletForm.Data) = {
    val hex: String = WalletMaker.hexStringGen
    val mnemonic: List[String] = genMnemonic(hex)
    val binaryKey: BinaryData = genSeed(mnemonic)
    val privKey = WalletMaker.privateKeyGen(binaryKey)
    val pubKey = WalletMaker.publicKeyUncompressed(privKey)
    val pubKeyAddress = WalletMaker.pubKeyUncompressed(pubKey, bitcoinNetwork match {
      case "regtest" => Base58.Prefix.PubkeyAddressTestnet
      case "testnet" => Base58.Prefix.PubkeyAddressTestnet
    })

    SnailWallet(transData, Seed(mnemonic, binaryKey.toString), privKey.toString, pubKey.toString, pubKeyAddress)
  }
}
