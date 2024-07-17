package APIs.TaskAPI

import Common.API.API
import Global.ServiceCenter.taskServiceCode
import io.circe.Decoder

abstract class TaskMessage[ReturnType:Decoder] extends API[ReturnType](taskServiceCode)
