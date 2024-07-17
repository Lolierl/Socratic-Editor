package APIs.TaskAPI

case class AddTaskIdentityMessage(taskName:String, userName:String, identity:String) extends TaskMessage[String]
