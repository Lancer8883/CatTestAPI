import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App{
    implicit val system = ActorSystem("test")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) => HttpResponse(entity = "jafar sasi")

    case HttpRequest(GET, Uri.Path("/cat"), _, _, _) => {
      val req: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://aws.random.cat/meow"))
      req.onComplete {
        case Success(res) => println(res);
        case Failure(_n) => println(_n)
      }
      HttpResponse(entity = "test")
    }

    case r: HttpRequest =>
      r.discardEntityBytes()
      HttpResponse(404, entity = "Unknown resource!")
  }

    val bindingFuture = Http().bindAndHandleSync(requestHandler, "localhost", 8080)

    println("Server online at http://localhost:8080/")
}