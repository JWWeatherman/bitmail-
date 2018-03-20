package model

import com.google.inject.Inject
import forms.CreateWalletForm
import fr.acinq.bitcoin.Crypto.PublicKey
import model.models.SnailWallet
import play.api.libs.json._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor

import scala.collection.SeqLike

class WalletStorage @Inject()(mongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) {

  def toBSON(json: JsValue): BSONDocument =
    reactivemongo.play.json.BSONFormats.BSONDocumentFormat.reads(json).get

  def toBSON[T](o: T)(implicit tjs: Writes[T]): BSONDocument =
    reactivemongo.play.json.BSONFormats.BSONDocumentFormat.reads(Json.toJson[T](o)).get

  val collectionLabel = "wallets"

  import SnailWallet._
  import CreateWalletForm.Data._

  def insertWallet(wallet: SnailWallet) = {
    for {
      db <- mongoApi.database
      result <- db.collection[BSONCollection](collectionLabel).insert(wallet)
    } yield result
  }

  def deleteWallet(publicKey: String) = {
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .remove(BSONDocument(SnailWallet.publicKeyField -> publicKey))
    } yield result
  }

  def findWallet(publicKey: String) = {
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .find(BSONDocument(SnailWallet.publicKeyField -> BSONDocument("$eq" -> publicKey)))
        .one[Option[SnailWallet]]
    } yield result.flatten
  }

  def findAllWallets = {
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .find(BSONDocument())
        .cursor[SnailWallet]()
        .collect(-1, Cursor.ContOnError[List[SnailWallet]]((list, exeption) => {}))
    } yield result
  }

  implicit val f3 = Json.format[(String, String)]
  implicit val f1 = Json.format[(String, (String, String))]
  implicit val f4 = Json.format[(String, Boolean)]
  implicit val f2 = Json.format[(String, (String, Boolean))]
  implicit val f6 = new Writes[(String, Seq[(String, String)])] {
    override def writes(o: (String, Seq[(String, String)])): JsValue = {
      JsObject(Seq((o._1, JsArray(o._2.map(v => Json.toJson(v))))))
    }
  }

  def findWalletByEmail(email: String) = {
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .find(
          toBSON(
            "$or" -> List(s"$transDataField.$recipientEmailField" -> email,
                          s"$transDataField.$senderEmailField" -> email)
          )
        )
        .one[Option[SnailWallet]]
    } yield result.flatten
  }

  def markWalletBounced(wallet: SnailWallet) = {
    val selector = toBSON(
      JsObject(
        Seq(
          SnailWallet.publicKeyAddressField -> JsObject(
            Seq("$eq" -> JsString(wallet.publicKeyAddress))
          )
        )
      )
    )

    val t = toBSON(Json.toJson(SnailWallet.publicKeyAddressField -> ("$eq" -> wallet.publicKeyAddress)))

    val update = toBSON(
      JsObject(
        Seq(
          "$set" -> JsObject(
            Seq(
              SnailWallet.bouncedField -> JsBoolean(false)
            )
          )
        )
      )
    )

    val u = toBSON(Json.toJson("$set" -> (SnailWallet.bouncedField -> false)))

    for {
      db <- mongoApi.database
      result <- db.collection[BSONCollection](collectionLabel).update(t, update)
    } yield result
  }

}
