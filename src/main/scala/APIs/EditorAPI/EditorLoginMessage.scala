package APIs.EditorAPI

case class EditorLoginMessage(userName: String, password: String) extends EditorMessage[String]

