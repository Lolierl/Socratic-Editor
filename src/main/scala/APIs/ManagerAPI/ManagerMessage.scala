package APIs.ManagerAPI

import Common.API.API
import Global.ServiceCenter.managerServiceCode
import io.circe.Decoder

abstract class ManagerMessage[ReturnType:Decoder] extends API[ReturnType](managerServiceCode)
