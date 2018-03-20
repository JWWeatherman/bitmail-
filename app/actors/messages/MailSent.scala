package actors.messages

import email.EmailMessage

case class MailSent(sendMessage: EmailMessage)
