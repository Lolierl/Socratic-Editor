package APIs.ManagerAPI

case class ManagerLoginMessage(userName: String, password: String) extends ManagerMessage[String]

