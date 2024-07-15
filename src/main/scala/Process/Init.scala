package Process

import Common.API.{API, PlanContext, TraceID}
import Global.{ServerConfig, ServiceCenter}
import Common.DBAPI.{initSchema, readDBBoolean, writeDB}
import Common.Object.SqlParameter
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.client.Client

import scala.io.Source
import java.util.UUID

def readFileToString(filePath: String): String = {
  val source = Source.fromFile(filePath)
  try {
    source.mkString
  } finally {
    source.close()
  }
}

object Init {
  def init(config:ServerConfig):IO[Unit]=

    val currentDir = System.getProperty("user.dir")
    val relativePath = s"src/main/scala/Shared/default_profile.txt"
    val fullPath = s"$currentDir/$relativePath"
    val defaultImage = readFileToString(fullPath)

    given PlanContext = PlanContext(traceID = TraceID(UUID.randomUUID().toString), 0)

    val checkDefaultExists = readDBBoolean(
      s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.user_photo WHERE user_name = ?)",
      List(SqlParameter("String", "default"))
    )

    val defaultWrite = checkDefaultExists.flatMap { exists =>
      if (!exists) {
        writeDB(s"INSERT INTO ${schemaName}.user_photo (user_name, profile_photo) VALUES (?, ?)",
          List(
            SqlParameter("String", "default"), SqlParameter("String", defaultImage)
          )
        )
      } else {
        IO.pure("OK")
      }
    }
      for {
        _ <- API.init(config.maximumClientConnection)
        _ <- initSchema(schemaName)
        _ <- writeDB(s"CREATE TABLE IF NOT EXISTS ${schemaName}.users (user_name TEXT, password TEXT, sur_name TEXT, last_name TEXT, institute TEXT, expertise TEXT, email TEXT, periodical TEXT, validation BOOLEAN)", List())
        _ <- writeDB(s"CREATE TABLE IF NOT EXISTS ${schemaName}.user_photo (user_name TEXT, profile_photo TEXT)", List())
        _ <- defaultWrite
      } yield ()
}
