package actors.messages

import email.EmailMessage

case class MailFailed(reason: String, sendmessage: EmailMessage)