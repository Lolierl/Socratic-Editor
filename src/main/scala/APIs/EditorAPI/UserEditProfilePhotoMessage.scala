package APIs.EditorAPI

case class UserEditProfilePhotoMessage(userName: String, Base64Image:String) extends EditorMessage[String]
