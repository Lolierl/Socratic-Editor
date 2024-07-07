package Impl

import APIs.PatientAPI.PatientQueryMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*


case class EditorReadTasksMessagePlanner(EditorName:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {
    val TasksIO: IO[List[Json]] = readDBRows(
      s"SELECT TaskName FROM ${schemaName}.Tasks WHERE EditorName = ?",
      List(SqlParameter("String", EditorName))
    )
    TasksIO.map { managerTasks =>
      Json.arr(managerTasks: _*).noSpaces
    }
  }
