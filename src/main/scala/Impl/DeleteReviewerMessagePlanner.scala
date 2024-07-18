package Impl

import APIs.TaskAPI.AddTaskIdentityMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import Global.GlobalVariables.maximumReviewers
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.parser.parse

import scala.util.Random

case class DeleteReviewerMessagePlanner(userName: String, Periodical:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {
    val checkUserExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.reviewers WHERE periodical = ? AND user_name = ?)",
      List(SqlParameter("String", Periodical),
        SqlParameter("String", userName))
    )
    checkUserExists.flatMap { exists =>
      if (!exists) {
        IO.pure("Doesn't exist")
      } else {
        writeDB(
          s"DELETE FROM ${schemaName}.reviewers WHERE periodical = ? AND user_name = ?",
          List(SqlParameter("String", Periodical),
            SqlParameter("String", userName))
        )
      }

    }
  }
