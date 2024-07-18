package Impl

import APIs.TaskAPI.AddTaskIdentityMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import Global.GlobalVariables.maximumReviewers
import Global.GlobalVariables.ReviewersPerArticle
import cats.effect.IO
import io.circe.Json
import io.circe.parser.parse
import io.circe.generic.auto.*
import scala.util.Random
import cats.implicits._
import APIs.TaskAPI.ReadTaskAuthorMessage

case class AllocateReviewerMessagePlanner(taskName: String, Periodical: String, override val planContext: PlanContext) extends Planner[String] {

  override def plan(using PlanContext): IO[String] = {
    val GetAuthor = ReadTaskAuthorMessage(taskName).send
    val getReviewers = readDBRows(
      s"SELECT user_name FROM ${schemaName}.reviewers WHERE periodical = ?",
      List(SqlParameter("String", Periodical))
    )

    for {
      author <- GetAuthor
      rows <- getReviewers
      reviewers = rows.flatMap { row =>
        parse(row.toString).toOption.flatMap(_.hcursor.get[String]("userName").toOption)
      }
      reviewersWithoutAuthor = reviewers.filterNot(_ == author)
      selectedReviewers = Random.shuffle(reviewersWithoutAuthor).take(ReviewersPerArticle)
      sendMessages = selectedReviewers.map { reviewer =>
        AddTaskIdentityMessage(taskName, reviewer, "reviewer").send
      }
      result <- sendMessages.parSequence.map(_ => "Messages sent successfully")
    } yield result
  }
}