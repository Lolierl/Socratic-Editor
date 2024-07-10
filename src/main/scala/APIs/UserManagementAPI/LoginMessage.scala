package APIs.UserManagementAPI

case class LoginMessage(userName: String, password: String) extends UserManagementMessage[String]

