package Impl

import cats.effect.IO
import io.circe.generic.auto.*
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import APIs.PatientAPI.PatientQueryMessage
import cats.effect.IO
import io.circe.generic.auto.*
import APIs.ManagerAPI.AuthenEditorMessage

case class EditorRegisterMessagePlanner(userName: String, password: String,override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    startTransaction {
      val checkUserExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.user_name WHERE user_name = ?)",
        List(SqlParameter("String", userName))
      )

      checkUserExists.flatMap { exists =>
        if (exists) {
          IO.raiseError(new Exception("already registered"))
        } else {
          val insertUser = writeDB(
            s"INSERT INTO ${schemaName}.user_name (user_name, password, validation) VALUES (?, ?, FALSE)",
            List(SqlParameter("String", userName), SqlParameter("String", password))
          )
          val sendAuthMessage = AuthenEditorMessage(userName).send

          insertUser *> sendAuthMessage.as("User registered successfully")
        }
      }
    }
  }

