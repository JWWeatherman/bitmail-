package actors

import java.io.File

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.google.inject.Inject
import com.google.inject.name.Named
import fr.acinq.bitcoin.Crypto.PublicKey
import messages.BitcoinTransactionReceived
import play.api.libs.mailer.MailerClient

@Named("NotificationSendingActor")
class NotificationSendingActor @Inject()(mailerClient: MailerClient) extends Actor {
  override def receive: Receive = {
    case bitcoinReceived: BitcoinTransactionReceived =>
      val file = new File(
        s"./coinsReceived-${bitcoinReceived.transData.recipientEmail}-${bitcoinReceived.newValue.value - bitcoinReceived.previousValue.value}.txt"
      )
      if (file.exists()) file.delete()
      file.createNewFile()
      fundsReceiveRecipient(bitcoinReceived)
      fundsReceivedSender(bitcoinReceived)
  }

  private def fundsReceiveRecipient(bitcoinReceived: BitcoinTransactionReceived): Unit = {
    import email._
    val recipientEmail = new RecipientEmail(mailerClient)
    recipientEmail.send(
      "fundsReceiveRecipient",
      bitcoinReceived.transData.recipientEmail,
      bitcoinReceived.transData.senderEmail,
      bitcoinReceived.publicKeyAddress,
      bitcoinReceived.transData.senderMessage,
      bitcoinReceived.newValue.toFriendlyString
    )
  }

  private def fundsReceivedSender(bitcoinReceived: BitcoinTransactionReceived): Unit = {
    import email._
    val recipientEmail = new SenderEmail(mailerClient)
    recipientEmail.send(
      "fundsReceivedSender",
      bitcoinReceived.transData.recipientEmail,
      bitcoinReceived.transData.senderEmail,
      bitcoinReceived.publicKeyAddress,
      bitcoinReceived.transData.senderMessage,
      bitcoinReceived.newValue.toFriendlyString
    )
  }
}
