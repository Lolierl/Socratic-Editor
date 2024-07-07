package Impl

import APIs.PatientAPI.PatientQueryMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*


case class EditorReceiveTaskMessagePlanner(TaskName: String, EditorName:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    writeDB(
      s"INSERT INTO ${schemaName}.Tasks (TaskName, EditorName) VALUES (?, ?)",
      List(
        SqlParameter("String", TaskName),
        SqlParameter("String", EditorName)
      ))
  }
