package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, Macros}

/*
 * seed data
 * @property mnemonic {Seq[String]} 12 backup word
 * @property binaryKey {String} binary ket created from mnemonic
 * */

case class Seed(mnemonic: Seq[String], binaryKey: String)

object Seed {
  implicit val jsonFormat = Json.format[Seed]

  private val mnemonicField = "mnemonic"
  private val binaryKeyField = "binaryKey"

  val seedReads: Reads[Seed] = (
    (JsPath \ mnemonicField).read[Seq[String]] and
    (JsPath \ binaryKeyField).read[String]
  )(Seed.apply _)

  val seedWrites: Writes[Seed] = (
    (JsPath \ mnemonicField).write[Seq[String]] and
    (JsPath \ binaryKeyField).write[String]
  )(unlift(Seed.unapply))

  implicit val seedFormat: Format[Seed] =
    Format(seedReads, seedWrites)

  /*
  implicit object SeedWriter extends BSONDocumentWriter[Seed] {
    override def write(t : Seed) : BSONDocument = BSONDocument(
      mnemonicField -> t.mnemonic,
      binaryKeyField -> t.binaryKey
    )
  }
   */

  implicit val seedHandler = Macros.handler[Seed]
}
