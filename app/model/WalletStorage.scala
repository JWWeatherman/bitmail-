package model

import com.google.inject.Inject
import fr.acinq.bitcoin.Crypto.PublicKey
import model.models.SnailWallet
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor

class WalletStorage @Inject()(mongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) {

  val collectionLabel = "wallets"

  import SnailWallet._

  def insertWallet(wallet: SnailWallet) =
    for {
      db <- mongoApi.database
      result <- db.collection[BSONCollection](collectionLabel).insert(wallet)
    } yield result

  def deleteWallet(publicKey: String) =
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .remove(BSONDocument(SnailWallet.publicKeyField -> publicKey))
    } yield result

  def findWallet(publicKey: String) =
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .find(BSONDocument(SnailWallet.publicKeyField -> BSONDocument("$eq" -> publicKey)))
        .one[Option[SnailWallet]]
    } yield result.flatten

  def findAllWallets =
    for {
      db <- mongoApi.database
      result <- db
        .collection[BSONCollection](collectionLabel)
        .find(BSONDocument())
        .cursor[SnailWallet]()
        .collect(-1, Cursor.ContOnError[List[SnailWallet]]((list, exeption) => {}))
    } yield result

}
