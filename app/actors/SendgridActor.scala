package actors

import actors.messages._
import akka.actor.Actor
import akka.actor.Actor.Receive
import com.google.inject.Inject
import com.sendgrid._
import email.{ EmailMessage, SendGridConfiguration }
import play.api.libs.json._

class SendgridActor @Inject()(config: SendGridConfiguration) extends Actor {

  implicit val brr = Json.format[BounceRecords]

  override def receive : Receive = {
    case emailBounceCheck : EmailBounceCheck =>
      val sg = new SendGrid(config.secretKey)
      val request = new Request()
      request.setMethod(Method.GET)
      request.setEndpoint("suppression/bounces")
      val response = sg.api(request)
      for {
        records <- Json.parse(response.getBody).validate[List[BounceRecords]].asOpt
      } yield {
        sender ! EmailBounceNotification(records)
      }

    case sendMessage : EmailMessage =>
      val fr = new com.sendgrid.Email(sendMessage.from)
      val subject = "Bitmail"
      val content = new Content("text/html", sendMessage.asHtml)
      val sg = new SendGrid(config.secretKey)
      val mail = new Mail(fr, subject, new com.sendgrid.Email(sendMessage.to), content)
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
        case x if 200 until 299 contains x => sender() ! MailSent(sendMessage)
        case x => sender ! MailFailed(s"StatusCode: $x", sendMessage)
      }

    case x : Any =>
      val i = x

  }
}
