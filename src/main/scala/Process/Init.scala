package Process

import Common.API.{API, PlanContext, TraceID}
import Global.{ServerConfig, ServiceCenter}
import Common.DBAPI.{initSchema, writeDB}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.client.Client

import java.util.UUID

object Init {
  def init(config:ServerConfig):IO[Unit]=
    given PlanContext=PlanContext(traceID = TraceID(UUID.randomUUID().toString),0)
    for{
      _ <- API.init(config.maximumClientConnection)
      _ <- initSchema(schemaName)
      _ <- writeDB(s"CREATE TABLE IF NOT EXISTS ${schemaName}.user_name (user_name TEXT, password TEXT, sur_name TEXT, last_name TEXT, institute TEXT, expertise TEXT, email TEXT, periodical TEXT, validation BOOLEAN)", List())
      _ <- writeDB(s"CREATE TABLE IF NOT EXISTS ${schemaName}.Tasks (TaskName TEXT, EditorName TEXT)", List())
    } yield ()

}
