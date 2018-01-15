package email

import play.api.libs.mailer._

trait Email {
  def sendMailBatch(mailerClient: MailerClient, too: Seq[String], from: String, template: String): Unit = {
    val email = Email(
      "Bitmail",
      "duncan.nevin@gmail.com", // <--- needs to be email associated with mail jet account
      too,
      bodyHtml = Some(template)
    )
    mailerClient.send(email)
  }
}
