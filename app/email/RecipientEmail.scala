package email

import play.api.libs.mailer._

class RecipientEmail(mailerClient: MailerClient) extends Email {

  def send(recipientEmail: String, senderEmail: String, message: String, amount: String) = {
    val template = recipientTemplate(recipientEmail, senderEmail, message, amount)
    sendMailBatch(mailerClient, Seq(recipientEmail), senderEmail, template)
  }

  def recipientTemplate(recipientEmail: String, senderEmail: String, message: String, amount: String): String = {
    s"""
       |<html>
       |  <body>
       |    <p>Hello $recipientEmail,</p>
       |    <p>$senderEmail has sent you $amount USD worth of Bitcoin!</p>
       |    <br/>
       |    <p>$message</p>
       |    <br/>
       |    <p>Please go to the following link to learn how to claim this bitcoin.</p>
       |    <a href="http://localhost:9000/#/recipient/$recipientEmail">Claim your coin!</a>
       |  </body>
       |</html>
    """.stripMargin
  }
}
