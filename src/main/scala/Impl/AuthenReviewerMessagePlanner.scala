package Impl

import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*
import APIs.TaskAPI.AddTaskIdentityMessage


case class AuthenReviewerMessagePlanner(taskName:String, userName: String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {

    AddTaskIdentityMessage(taskName, userName, "reviewer").send
  }
