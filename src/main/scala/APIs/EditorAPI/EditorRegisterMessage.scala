package APIs.EditorAPI

import Shared.EditorInfo

case class EditorRegisterMessage(editorInfo: EditorInfo) extends EditorMessage[String]

