package APIs.ManagerAPI

case class AuthenEditorMessage(userName: String, periodical: String) extends ManagerMessage[String]

