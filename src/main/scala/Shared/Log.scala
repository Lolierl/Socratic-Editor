package Shared

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import cats.effect.IO
import io.circe.parser.decode

object Decision extends Enumeration {
  type Decision = Value
  val Review, Reject, Revise = Value
  implicit val decisionEncoder: Encoder[Decision] = Encoder.encodeString.contramap[Decision](_.toString)
  implicit val decisionDecoder: Decoder[Decision] = Decoder.decodeString.emap { str =>
    Decision.values.find(_.toString == str).toRight(s"Invalid Decision value: $str")
  }
}

import Decision._

case class Log(
                           logType:String,
                           userName: String,
                           comment: String,
                           decision: Decision,
                           reasonsToAccept: String,
                           reasonsToReject: String,
                           questionsToAuthors: String,
                           rating: Int,
                           confidence: Int
                         )
