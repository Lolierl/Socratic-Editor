package APIs.TaskAPI

case class ReadTaskPDFMessage(taskName:String) extends TaskMessage[String]
