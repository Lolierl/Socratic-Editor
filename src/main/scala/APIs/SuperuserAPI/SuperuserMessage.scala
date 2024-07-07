package APIs.SuperuserAPI

import Common.API.API
import Global.ServiceCenter.superuserServiceCode
import io.circe.Decoder

abstract class SuperuserMessage[ReturnType:Decoder] extends API[ReturnType](superuserServiceCode)
