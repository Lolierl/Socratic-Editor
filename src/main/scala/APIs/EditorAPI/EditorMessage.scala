package APIs.EditorAPI

import Common.API.API
import Global.ServiceCenter.editorServiceCode
import io.circe.Decoder

abstract class EditorMessage[ReturnType:Decoder] extends API[ReturnType](editorServiceCode)
