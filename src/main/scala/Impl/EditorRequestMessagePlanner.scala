package Impl

import APIs.UserManagementAPI.RegisterMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*


case class EditorRequestMessagePlanner(userName: String, allowed:Boolean, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    if (allowed) {
        for {
          _ <- writeDB(
            s"UPDATE ${schemaName}.users SET validation = TRUE WHERE user_name = ?",
            List(SqlParameter("String", userName))
          )
          passwordHash <- readDBString(
            s"SELECT password_hash FROM ${schemaName}.key_buffer WHERE user_name = ?",
            List(SqlParameter("String", userName))
          )
          salt <- readDBString(
            s"SELECT salt FROM ${schemaName}.key_buffer WHERE user_name = ?",
            List(SqlParameter("String", userName))
          )
          _ <- writeDB(
            s"DELETE FROM ${schemaName}.key_buffer WHERE user_name = ?",
            List(SqlParameter("String", userName))
          )
          result <- RegisterMessage(userName, passwordHash, salt, "editor").send
        } yield "OK"
    } else {
      for {
        _ <- writeDB(
          s"DELETE FROM ${schemaName}.users WHERE user_name = ?",
          List(SqlParameter("String", userName))
        )
        _ <- writeDB(
          s"DELETE FROM ${schemaName}.key_buffer WHERE user_name = ?",
          List(SqlParameter("String", userName))
        )
      } yield "User deleted"
    }
  }

