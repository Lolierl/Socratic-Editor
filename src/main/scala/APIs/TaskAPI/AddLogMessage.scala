package APIs.TaskAPI

import Shared.Log

case class AddLogMessage(taskName:String, log:Log) extends TaskMessage[String]
