package APIs.TaskAPI

case class ReadTaskInfoMessage(taskName:String, property:String) extends TaskMessage[String]
