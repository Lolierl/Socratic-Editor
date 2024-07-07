package Impl

import APIs.PatientAPI.PatientQueryMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*


case class EditorRequestMessagePlanner(userName: String, allowed:Boolean, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    val checkUserExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.user_name WHERE user_name = ?)",
        List(SqlParameter("String", userName))
      )

    checkUserExists.flatMap { exists =>
      if (exists) {
        IO.raiseError(new Exception("already registered"))
      } else {
        if (allowed) {
          writeDB(
            s"UPDATE ${schemaName}.user_name SET validation = True WHERE user_name = ?",
            List(SqlParameter("String", userName))
          ).as("Validation set to True")
        } else {
          writeDB(
            s"DELETE FROM ${schemaName}.user_name WHERE user_name = ?",
            List(SqlParameter("String", userName))
          ).as("User deleted")
        }
      }
    }
  }

