package APIs.EditorAPI

case class EditorRequestMessage(userName: String, allowed:Boolean) extends EditorMessage[String]

