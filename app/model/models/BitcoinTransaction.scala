package model.models

import org.bitcoinj.core.Sha256Hash
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class BitcoinTransaction(publicAddress: String, transactionId: String)

object BitcoinTransaction {
  val publicAddressField = "publicAddress"
  val transactionIdField = "transactionId"

  implicit object BitcoinTransactionWriter extends BSONDocumentWriter[BitcoinTransaction] {
    override def write(t: BitcoinTransaction): BSONDocument = BSONDocument(
      publicAddressField -> t.publicAddress,
      transactionIdField -> t.transactionId
    )
  }

  implicit object BitcoinTransactionReader extends BSONDocumentReader[Option[BitcoinTransaction]] {
    override def read(bson: BSONDocument) =
      for {
        publicAddress <- bson.getAs[String](publicAddressField)
        transactionId <- bson.getAs[String](transactionIdField)
      } yield BitcoinTransaction(publicAddress, transactionId)
  }

}
