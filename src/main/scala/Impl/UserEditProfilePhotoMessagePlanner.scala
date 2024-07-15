package Impl

import APIs.UserManagementAPI.RegisterMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*

case class UserEditProfilePhotoMessagePlanner(userName:String, Base64Image: String, override val planContext: PlanContext) extends Planner[String] {
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    val checkPhotoExists = readDBBoolean(
      s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.user_photo WHERE user_name = ?)",
      List(SqlParameter("String", userName))
    )

    checkPhotoExists.flatMap { exists =>
      if (exists) {
        writeDB(
          s"UPDATE ${schemaName}.user_photo SET profile_photo = ? WHERE user_name = ?",
          List(
            SqlParameter("String", Base64Image),
            SqlParameter("String", userName)
          )
        ).map(_ => "OK")
      } else {
          writeDB(
            s"INSERT INTO ${schemaName}.user_photo (profile_photo, user_name) VALUES (?, ?)",
            List(
              SqlParameter("String", Base64Image),
              SqlParameter("String", userName)
            )
          ).map( _ => "OK")
      }
    }
  }
}
