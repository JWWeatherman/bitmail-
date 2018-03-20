package actors.messages

case class EmailBounceNotification(records: List[BounceRecords])
