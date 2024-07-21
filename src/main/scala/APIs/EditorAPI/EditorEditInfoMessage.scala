package APIs.EditorAPI

case class EditorEditInfoMessage(userName:String, property:String, updateValue: String) extends EditorMessage[String]