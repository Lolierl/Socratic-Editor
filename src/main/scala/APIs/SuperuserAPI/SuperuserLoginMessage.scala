package APIs.SuperuserAPI

case class SuperuserLoginMessage(userName: String, password: String) extends SuperuserMessage[String]

