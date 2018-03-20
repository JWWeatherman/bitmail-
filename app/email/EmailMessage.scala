package email

trait EmailMessage {
 def asHtml : String
  def from : String
  def to : String
}
