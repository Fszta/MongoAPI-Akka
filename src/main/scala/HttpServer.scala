import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import utils.LoggerBase
import routes.RouteController._

object HttpServer  extends LoggerBase {
  val port = 8080
  val host = "localhost"

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("MongoRestAPI")
    implicit val actorMaterializer = ActorMaterializer()

    /** Define entry route of the application */
    lazy val routes: Route = pathPrefix("api") {
      mongoRoutes
    }

    /** Start http server */
    Http().bindAndHandle(routes, host, port)
    writeLog("info", "Start http server at %s on port %s"
      .format(host, port))
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }
}
