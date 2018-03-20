package email

import com.google.inject.Inject
import play.api.libs.mailer._

class EmailWithMailer @Inject() (mailerClient: MailerClient) extends Email {

  override def sendMail(to : String, from : String, template : String) : Boolean = {
    val email = Email(
      "Bitmail",
      "doohickeymastermind@gmail.com", // <--- needs to be email associated with mail account in app config
      Seq(to),
      bodyHtml = Some(template)
    )
    val messageId = mailerClient.send(email)
    !messageId.isEmpty
  }
}
