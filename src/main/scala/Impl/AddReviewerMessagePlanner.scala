package Impl

import APIs.TaskAPI.AddTaskIdentityMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import Global.GlobalVariables.maximumReviewers
import cats.effect.IO
import io.circe.Json
import io.circe.parser.parse
import io.circe.generic.auto.*
import scala.util.Random
import APIs.TaskAPI.AddTaskIdentityMessage

case class AddReviewerMessagePlanner(taskName: String, Periodical:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {
    val getReviewers = readDBRows(s"SELECT userName FROM ${schemaName}.reviewers WHERE periodical = ?",
      List(SqlParameter("String", Periodical))
    )

    getReviewers.flatMap{rows => 
    {
        val reviewers = rows.flatMap { row =>
          parse(row.toString).toOption.flatMap(_.hcursor.get[String]("userName").toOption)
        }

        val selectedReviewers = Random.shuffle(reviewers).take(3)
  
        // 对每个选出的评审员调用 AddTaskIdentityMessage(userName).send
        val sendMessages = selectedReviewers.map { reviewer =>
          AddTaskIdentityMessage(taskName, reviewer, "reviewer").send
        }
        IO.pure("Messages sent successfully")
    }
    }

  }