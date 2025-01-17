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
import APIs.UserManagementAPI.CheckUserExistsMessage
import Shared.PasswordHasher.hashPassword

case class EditorRegisterMessagePlanner(editorInfo: EditorInfo, password:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    startTransaction {
      val checkUserExists = CheckUserExistsMessage(editorInfo.userName).send

      checkUserExists.flatMap { exists =>
        if (exists) {
          IO.pure("already registered")
        } else {
          val (passwordHash, salt) = hashPassword(password)
          for {
            _ <- writeDB(
              s"INSERT INTO ${schemaName}.key_buffer (user_name, password_hash, salt) VALUES (?, ?, ?)",
              List(
                SqlParameter("String", editorInfo.userName),
                SqlParameter("String", passwordHash),
                SqlParameter("String", salt)
              ))
            _ <- writeDB(
              s"INSERT INTO ${schemaName}.users (user_name, sur_name, last_name, institute, expertise, email, periodical, validation) VALUES (?, ?, ?, ?, ?, ?, ?, FALSE)",
              List(
                SqlParameter("String", editorInfo.userName),
                SqlParameter("String", editorInfo.surName),
                SqlParameter("String", editorInfo.lastName),
                SqlParameter("String", editorInfo.institute),
                SqlParameter("String", editorInfo.expertise),
                SqlParameter("String", editorInfo.email),
                SqlParameter("String", editorInfo.periodical)
              ))
            _ <- AuthenEditorMessage(editorInfo.userName, editorInfo.periodical).send
          } yield "Editor registered successfully"
        }
      }
    }
  }

