package APIs.UserManagementAPI

import Common.API.API
import Global.ServiceCenter.usermanagementServiceCode
import io.circe.Decoder

abstract class UserManagementMessage[ReturnType:Decoder] extends API[ReturnType](usermanagementServiceCode)
