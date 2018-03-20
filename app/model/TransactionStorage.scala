package model

import com.google.inject.Inject
import model.models.BitcoinTransaction
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONWriter}
import reactivemongo.api.MongoConnection
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.{ExecutionContext, Future}

class TransactionStorage @Inject()(mongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) {
  val collectionLabel = "transactions"

  //import BitcoinTransaction.BitcoinTransactionWriter
  //import BitcoinTransaction.BitcoinTransactionReader

  def insertTransaction(transaction: BitcoinTransaction) =
    for {
      db <- mongoApi.database
      result <- db.collection[BSONCollection](collectionLabel).insert(transaction)
    } yield result

  def findTransactionByTransactionId(transactionId: String) =
    for {
      db <- mongoApi.database
      transaction <- db
        .collection[BSONCollection](collectionLabel)
        .find(BSONDocument(BitcoinTransaction.transactionIdField -> BSONDocument("$eq" -> transactionId)))
        .one[Option[BitcoinTransaction]]
    } yield transaction.flatten

  def findTransactionByPublicAddress(publicAddress: String) =
    for {
      db <- mongoApi.database
      transaction <- db
        .collection[BSONCollection](collectionLabel)
        .find(BSONDocument(BitcoinTransaction.publicAddressField -> BSONDocument("$eq" -> publicAddress)))
        .one[Option[BitcoinTransaction]]
    } yield transaction.flatten

}
