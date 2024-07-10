package APIs.UserManagementAPI

case class RegisterMessage(userName: String, password: String, usertype:String) extends UserManagementMessage[String]

