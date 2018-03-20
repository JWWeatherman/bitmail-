package model.models

import org.bitcoinj.core.Sha256Hash
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class BitcoinTransaction(publicAddressKey: String,
                              transactionId: String,
                              senderState: String,
                              recipientState: String)

object BitcoinTransaction {
  val publicAddressField = "publicAddress"
  val transactionIdField = "transactionId"
  val senderStateField = "senderState"
  val recipientStateField = "recipientState"

  val NotSent = "NotSent"
  val Sent = "Sent"
  val Bounced = "Bounced"

  implicit object BitcoinTransactionWriter extends BSONDocumentWriter[BitcoinTransaction] {
    override def write(t: BitcoinTransaction): BSONDocument = BSONDocument(
      publicAddressField -> t.publicAddressKey,
      transactionIdField -> t.transactionId,
      senderStateField -> t.senderState,
      recipientStateField -> t.recipientState
    )
  }

  implicit object BitcoinTransactionReader extends BSONDocumentReader[Option[BitcoinTransaction]] {
    override def read(bson: BSONDocument) =
      for {
        publicAddress <- bson.getAs[String](publicAddressField)
        transactionId <- bson.getAs[String](transactionIdField)
        senderState <- bson.getAs[String](senderStateField).orElse(Some(NotSent))
        recipientState <- bson.getAs[String](recipientStateField).orElse(Some(NotSent))
      } yield BitcoinTransaction(publicAddress, transactionId, senderState, recipientState)
  }
}
