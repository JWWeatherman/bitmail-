package actors

import java.io.File

import actors.messages.{BitcoinTransactionReceived, NotificationEmailSent}
import akka.actor.Actor
import com.google.inject.Inject
import com.google.inject.name.Named
import email.{RecipientEmail, SenderEmail}

@Named("NotificationSendingActor")
class NotificationSendingActor @Inject()(recipientEmail: RecipientEmail, senderEmail: SenderEmail) extends Actor {
  override def receive: Receive = {
    case bitcoinReceived: BitcoinTransactionReceived =>
      val file = new File(
        s"./coinsReceived-${bitcoinReceived.transData.recipientEmail}-${bitcoinReceived.newValue.value - bitcoinReceived.previousValue.value}.txt"
      )
      if (file.exists()) file.delete()
      file.createNewFile()
      this.sender() ! NotificationEmailSent(bitcoinReceived.transactionId,
                                            fundsReceiveRecipient(bitcoinReceived),
                                            fundsReceivedSender(bitcoinReceived))
  }

  private def fundsReceiveRecipient(bitcoinReceived: BitcoinTransactionReceived): Boolean = {
    recipientEmail.send(
      "fundsReceiveRecipient",
      bitcoinReceived.transData.recipientEmail,
      bitcoinReceived.transData.senderEmail,
      bitcoinReceived.publicKeyAddress,
      bitcoinReceived.transData.senderMessage,
      bitcoinReceived.newValue.toFriendlyString
    )
  }

  private def fundsReceivedSender(bitcoinReceived: BitcoinTransactionReceived): Boolean = {
    senderEmail.send(
      "fundsReceivedSender",
      bitcoinReceived.transData.recipientEmail,
      bitcoinReceived.transData.senderEmail,
      bitcoinReceived.publicKeyAddress,
      bitcoinReceived.transData.senderMessage,
      bitcoinReceived.newValue.toFriendlyString
    )
  }
}
