package Impl

import cats.effect.IO
import io.circe.generic.auto.*
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*
import APIs.ManagerAPI.AuthenEditorMessage
import Shared.EditorInfo

case class EditorRegisterMessagePlanner(editorInfo: EditorInfo, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    startTransaction {
      val checkUserExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.users WHERE user_name = ?)",
        List(SqlParameter("String", editorInfo.userName))
      )

      checkUserExists.flatMap { exists =>
        if (exists) {
          IO.pure("already registered")
        } else {
          val insertUser = writeDB(
            s"INSERT INTO ${schemaName}.users (user_name, password, sur_name, last_name, institute, expertise, email, periodical, validation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSE)",
            List(
              SqlParameter("String", editorInfo.userName),
              SqlParameter("String", editorInfo.password),
              SqlParameter("String", editorInfo.surName),
              SqlParameter("String", editorInfo.lastName),
              SqlParameter("String", editorInfo.institute),
              SqlParameter("String", editorInfo.expertise),
              SqlParameter("String", editorInfo.email),
              SqlParameter("String", editorInfo.periodical)
            ))
          val sendAuthMessage = AuthenEditorMessage(editorInfo.userName, editorInfo.periodical).send

          insertUser *> sendAuthMessage.as("Editor registered successfully")
        }
      }
    }
  }

