package actors

import actors.messages.{ EmailBounceCheck, EmailBounceNotification, MailSent }
import akka.actor.{ Actor, ActorRef }
import akka.actor.Actor.Receive
import com.google.inject.Inject
import com.google.inject.name.Named
import email.BounceNotificationEmailMessage
import loggers.BitSnailLogger
import model.WalletStorage

import scala.concurrent.ExecutionContext

class EmailCommunicationsActor @Inject() (
  walletStorage                          : WalletStorage,
  logger                                 : BitSnailLogger,
  @Named(ActorNames.Mail) mailActor : ActorRef
  )(implicit ec: ExecutionContext) extends Actor {
  override def receive : Receive = {
    case bounceCheck : EmailBounceCheck =>
      mailActor ! bounceCheck

    case bounces : EmailBounceNotification =>
      var i = for {
        record <- bounces.records
      } yield
        for {
          optWallet <- walletStorage.findWalletByEmail(record.email)
        } yield {
          (optWallet, optWallet.flatMap(_.transData.senderEmail)) match {
            case (Some(wallet), Some(sender)) =>
              mailActor ! BounceNotificationEmailMessage(wallet.transData.recipientEmail, sender, wallet)
            case (Some(wallet), None) =>
              logger.MailBouncedWithAnonymousSender(wallet.transData.recipientEmail)
            case (None, None) =>
              logger.MailBounceForUnknownWallet(record.email)
            case _ =>
              var i = 0
          }

        }
    case mailSent : MailSent => mailSent.sendMessage match {
      case BounceNotificationEmailMessage(_, _, wallet) => for {
        result <- walletStorage.markWalletBounced(wallet)
      } yield {
        if (!result.ok)
          logger.CouldNotUpdateBounce(wallet)
      }
    }

  }

}


