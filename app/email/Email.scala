package email

trait Email {
  def sendMail(to: String, from: String, template: String) : Boolean
}
