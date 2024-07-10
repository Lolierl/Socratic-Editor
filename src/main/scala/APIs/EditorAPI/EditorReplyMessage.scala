package APIs.EditorAPI

case class EditorReplyMessage(TaskName: String, EditorName:String, ReplyContext:String) extends EditorMessage[String]

