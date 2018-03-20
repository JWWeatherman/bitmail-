package email
import com.google.inject.Inject
import com.sendgrid._

class EmailWithSendGrid @Inject()(config: SendGridConfiguration) extends Email {
  override def sendMail(to : String, from : String, template : String) : Boolean = {
    val fr = new com.sendgrid.Email(from)
    val subject = "Bitmail"
    val content = new Content("text/html", template)
    val sg = new SendGrid(config.secretKey)
    val mail = new Mail(fr, subject, new com.sendgrid.Email(to), content)
    mail.setTrackingSettings(
      {
        val t = new TrackingSettings
        t.setClickTrackingSetting({
          val s = new ClickTrackingSetting
          s.setEnable(false)
          s
        }
        )
        t
      }
    )
    val request = new Request()
    request.setMethod(Method.POST)
    request.setEndpoint("mail/send")
    request.setBody(mail.build())
    val response = sg.api(request)
    response.getStatusCode match {
      case x if 200 until 299 contains x => true
      case _ => false
    }

  }
}
