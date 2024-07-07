package APIs.EditorAPI

case class EditorRegisterMessage(userName: String, password: String) extends EditorMessage[String]

