package Impl

import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*


case class EditorFindMessagePlanner(periodical: String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    val fetchUserNamesIO: IO[List[Json]] = readDBRows(
      s"SELECT user_name FROM ${schemaName}.users WHERE periodical = ?",
      List(SqlParameter("String", periodical))
    )

    fetchUserNamesIO.map {UserName =>
      Json.arr(UserName: _*).noSpaces
    }
  }
