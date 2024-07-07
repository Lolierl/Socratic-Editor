package APIs.EditorAPI

case class EditorReadInfoMessage(property: String, userName: String) extends EditorMessage[String]

