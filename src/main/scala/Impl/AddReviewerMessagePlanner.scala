package Impl

import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*
import Global.GlobalVariables.maximumReviewers
import APIs.TaskAPI.AddTaskIdentityMessage
import APIs.UserManagementAPI.CheckUserExistsMessage


case class AddReviewerMessagePlanner(userName: String, Periodical:String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using PlanContext): IO[String] = {
    val checkUserExists = CheckUserExistsMessage(userName).send
    checkUserExists.flatMap { exists =>
      if (!exists) {
        IO.pure(s"User $userName doesn't exist")
      } else {
        val checkReviewerNumber = readDBInt(s"SELECT COUNT(*) FROM ${schemaName}.reviewers WHERE periodical = ?",
          List(SqlParameter("String", Periodical))
        )

        checkReviewerNumber.flatMap { reviewerCount =>
          if (reviewerCount > maximumReviewers) {
            // Return message if the number of reviewers is already 10 or more
            IO.pure("already reach maximum reviewers")
          } else {
            // Check if the user is already authorized
            val checkIfAuthorized = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.reviewers WHERE periodical = ? AND user_name = ?)",
              List(SqlParameter("String", Periodical), SqlParameter("String", userName))
            )

            checkIfAuthorized.flatMap { isAuthorized =>
              if (isAuthorized) {
                // Return message if the user is already authorized
                IO.pure("already authorized")
              } else {
                // Add the user to the database
                val addReviewer = writeDB(s"INSERT INTO ${schemaName}.reviewers (user_name, periodical) VALUES (?, ?)",
                  List(SqlParameter("String", userName), SqlParameter("String", Periodical))
                )

                addReviewer.map { _ =>
                  // Return success message
                  "Successfully authorized"
                }
              }
            }
          }
        }
      }
    }
  }
