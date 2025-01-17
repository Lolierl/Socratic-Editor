package Impl

import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*


case class EditorReadInfoMessagePlanner(property: String, userName:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    readDBString(
      s"SELECT ${property} FROM ${schemaName}.users WHERE user_name = ?",
      List(SqlParameter("String", userName))
    )
  }
