package actors.messages

case class BounceRecords(created : Long, email: String, reason: String, status: String)
