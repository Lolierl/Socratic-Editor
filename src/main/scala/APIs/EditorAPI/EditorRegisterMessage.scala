package APIs.EditorAPI

import Shared.EditorInfo

case class EditorRegisterMessage(editorInfo: EditorInfo, password:String) extends EditorMessage[String]

