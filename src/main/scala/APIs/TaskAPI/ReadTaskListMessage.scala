package APIs.TaskAPI

case class ReadTaskListMessage(userName:String) extends TaskMessage[String]
