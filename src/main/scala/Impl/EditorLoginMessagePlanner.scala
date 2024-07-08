package Impl

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import APIs.PatientAPI.PatientQueryMessage
import cats.effect.IO
import io.circe.generic.auto.*


case class EditorLoginMessagePlanner(userName: String, password: String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    val checkUserExists = readDBBoolean(
      s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.users WHERE user_name = ?)",
      List(SqlParameter("String", userName))
    )

    checkUserExists.flatMap { userExists =>
      if (!userExists) {
        IO.pure("Invalid user")
      } else {
        readDBRows(
          s"SELECT user_name FROM ${schemaName}.users WHERE user_name = ? AND password = ?",
          List(SqlParameter("String", userName), SqlParameter("String", password))
        ).flatMap {
          case Nil => IO.pure("Wrong password")
          case _ =>
            // 验证是否允许登录
            readDBBoolean(
              s"SELECT validation FROM ${schemaName}.users WHERE user_name = ? AND password = ? AND validation = TRUE",
              List(SqlParameter("String", userName), SqlParameter("String", password))
            ).map { isValid =>
              if (isValid) "Valid user"
              else "Unauthorized"
            }
        }
      }
    }
  }
