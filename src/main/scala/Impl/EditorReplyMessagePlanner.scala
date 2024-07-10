package Impl

import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*


case class EditorReplyMessagePlanner(TaskName: String, EditorName:String, ReplyContext:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    readDBString(
      s"SELECT periodical FROM ${schemaName}.users WHERE user_name = ?",
      List(SqlParameter("String", EditorName))
    ).flatMap{periodical =>
      val checkTaskExists = readDBBoolean(
        s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.Tasks WHERE TaskName = ? AND Periodical = ?)",
        List(SqlParameter("String", TaskName),
          SqlParameter("String", periodical))
      )

      checkTaskExists.flatMap { TaskExists =>
        if (!TaskExists) {
          IO.pure("No such task")
        } else {
          writeDB(
            s"DELETE FROM ${schemaName}.Tasks WHERE TaskName = ?",
            List(SqlParameter("String", TaskName))
          ).as("Task Finished")
        }
      }
    }
  }
