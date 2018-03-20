package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import forms.CreateWalletForm
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

/*
 * Main wallet data to be stored during transaction
 * @property transData {CreateWalletForm.Data} form data sent from sender
 * @property seed {Seed} mnemonic and binary key
 * @property privateKey {String} private key
 * @property publicKey {String} public key
 * */

case class SnailWallet(transData: CreateWalletForm.Data,
                       seed: Seed,
                       privateKey: String,
                       publicKey: String,
                       publicKeyAddress: String)

object SnailWallet {

  val transDataField = "transData"
  val seedField = "seed"
  val privateKeyField = "privateKey"
  val publicKeyField = "publicKey"
  val publicKeyAddressField = "publicKeyAddress"

  implicit val jsonFormat = Json.format[SnailWallet]

  val walletReads: Reads[SnailWallet] = (
    (JsPath \ transDataField).read[CreateWalletForm.Data] and
    (JsPath \ seedField).read[Seed] and
    (JsPath \ privateKeyField).read[String] and
    (JsPath \ publicKeyField).read[String] and
    (JsPath \ publicKeyAddressField).read[String]
  )(SnailWallet.apply _)

  val walletWrites: Writes[SnailWallet] = (
    (JsPath \ transDataField).write[CreateWalletForm.Data] and
    (JsPath \ seedField).write[Seed] and
    (JsPath \ privateKeyField).write[String] and
    (JsPath \ publicKeyField).write[String] and
    (JsPath \ publicKeyAddressField).write[String]
  )(unlift(SnailWallet.unapply))

  implicit val walletFormat: Format[SnailWallet] =
    Format(walletReads, walletWrites)

  /*
  implicit object SnailWalletWriter extends BSONDocumentWriter[SnailWallet] {
      override def write(t : SnailWallet) : BSONDocument = BSONDocument(
      transDataField -> t.transData,
      seedField -> t.seed,
      privateKeyField -> t.privateKey,
      publicKeyField -> t.publicKey,
      publicKeyAddressField -> t.publicKeyAddress
    )
  }
   */

  implicit object SnailWalletReader extends BSONDocumentReader[Option[SnailWallet]] {
    override def read(bson: BSONDocument): Option[SnailWallet] =
      for {
        transData <- bson.getAs[CreateWalletForm.Data](transDataField)
        seed <- bson.getAs[Seed](seedField)
        privateKey <- bson.getAs[String](privateKeyField)
        publicKey <- bson.getAs[String](publicKeyField)
        publicKeyAddress <- bson.getAs[String](publicKeyAddressField)
      } yield SnailWallet(transData, seed, privateKey, publicKey, publicKeyAddress)
  }

  implicit val snailWalletHandler = Macros.handler[SnailWallet]
}
