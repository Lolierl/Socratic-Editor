package APIs.UserManagementAPI

case class RegisterMessage(userName: String, passwordHash:String, salt:String, usertype:String) extends UserManagementMessage[String]

