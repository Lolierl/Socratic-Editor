package APIs.TaskAPI

case class ReadPeriodicalTaskListMessage(periodicalName:String) extends TaskMessage[String]
