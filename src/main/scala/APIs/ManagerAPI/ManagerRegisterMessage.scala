package APIs.ManagerAPI

case class ManagerRegisterMessage(userName: String, password: String) extends ManagerMessage[String]

