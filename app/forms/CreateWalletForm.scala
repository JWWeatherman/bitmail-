package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

/**
 * The form which handles the submission of the credentials.
 */
object CreateWalletForm {

  /**
   * A play framework form.
   *
   * { "recipientEmail": "duncan.nevin@gmail.com",
   * "senderEmail": "duncan.nevin@gmail.com",
   * "senderMessage": "dfsaf",
   * "remainAnonymous": false
   * }
   */
  val form = Form(
    mapping(
      "recipientEmail" -> email,
      "senderEmail" -> optional(email),
      "senderMessage" -> text,
      "remainAnonymous" -> boolean
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param recipientEmail  The email of recipient
   * @param senderEmail     The password of the sender, can be left null if anonymous.
   * @param remainAnonymous Indicates if sender wants to remain anonymous.
   */
  case class Data(
      recipientEmail: String,
      senderEmail: Option[String],
      senderMessage: String,
      remainAnonymous: Boolean
  )

  object Data {

    val recipientEmailField = "recipientEmail"
    val senderEmailField = "senderEmail"
    val senderMessageField = "senderMessage"
    val remaindAnonymousField = "remainAnonymous"

    val dataReads: Reads[Data] = (
      (JsPath \ recipientEmailField).read[String] and
      (JsPath \ senderEmailField).readNullable[String] and
      (JsPath \ senderMessageField).read[String] and
      (JsPath \ remaindAnonymousField).read[Boolean]
    )(Data.apply _)

    val dataWrites: Writes[Data] = (
      (JsPath \ recipientEmailField).write[String] and
      (JsPath \ senderEmailField).writeNullable[String] and
      (JsPath \ senderMessageField).write[String] and
      (JsPath \ remaindAnonymousField).write[Boolean]
    )(unlift(Data.unapply))

    implicit val dataFormat: Format[Data] =
      Format(dataReads, dataWrites)

    /*
    implicit object WalletFormDataWriter extends BSONDocumentWriter[Data] {
      override def write(t : Data) : BSONDocument = BSONDocument(
        recipientEmailField -> t.recipientEmail,
        senderEmailField -> t.senderEmail,
        senderMessageField -> t.senderMessage,
        remaindAnonymousField -> t.remainAnonymous
      )
    }

    implicit object WalletFormDataReader extends BSONDocumentReader[Option[Data]] {
      override def read(bson : BSONDocument) : Option[Data] = for {
        recipientEmail <- bson.getAs[String](recipientEmailField)
        senderEmail <- bson.getAs[Option[String]](senderEmailField)
        senderMessage <- bson.getAs[String](senderMessageField)
        remainAnonymous <- bson.getAs[Boolean](remaindAnonymousField)
      } yield Data(recipientEmail, senderEmail, senderMessage, remainAnonymous)
    }
     */
    implicit val dataHandler = Macros.handler[Data]

  }

}
