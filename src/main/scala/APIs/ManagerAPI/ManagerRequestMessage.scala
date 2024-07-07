package APIs.ManagerAPI

case class ManagerRequestMessage(userName: String, allowed:Boolean) extends ManagerMessage[String]

