package actors.messages

case class NotificationEmailSent(transactionId: String, recipientSent : Boolean, senderSent : Boolean)
