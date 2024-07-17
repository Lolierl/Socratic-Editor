package APIs.TaskAPI

case class CheckTaskIdentityMessage(taskName:String, userName:String) extends TaskMessage[String]
