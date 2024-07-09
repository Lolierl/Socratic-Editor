package Impl

import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*


case class EditorReceiveTaskMessagePlanner(TaskName: String, Periodical:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    val checkPeriodicalExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.users WHERE periodical = ?)",
      List(SqlParameter("String", Periodical))
    )

    checkPeriodicalExists.flatMap { exists =>
      if (!exists) {
        IO.pure("No corresponding periodical or editor")
      } else {
        writeDB(
          s"INSERT INTO ${schemaName}.Tasks (TaskName, periodical) VALUES (?, ?)",
          List(
            SqlParameter("String", TaskName),
            SqlParameter("String", Periodical)
          ))
      }
    }
  }
