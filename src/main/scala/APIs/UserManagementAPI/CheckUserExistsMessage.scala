package APIs.UserManagementAPI

case class CheckUserExistsMessage(userName:String) extends UserManagementMessage[Boolean]
