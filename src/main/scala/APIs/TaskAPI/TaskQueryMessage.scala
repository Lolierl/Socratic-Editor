package APIs.TaskAPI

case class TaskQueryMessage(userName:String, taskName:String, periodicalName:String, pdfBase64:String, researchArea:String, Abstract:String, keyword:String, TLDR:String) extends TaskMessage[String]
