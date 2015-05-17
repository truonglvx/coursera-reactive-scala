package week5.linkchecker

import java.util.concurrent.Executor

import com.ning.http.client.AsyncHttpClient

import scala.concurrent.{Promise, Future}

trait WebClient {
  def get(url: String)(implicit  exec: Executor): Future[String]
}

case class BadStatus(status: Int) extends RuntimeException

object AsyncWebClient extends WebClient {

  private val client = new AsyncHttpClient

  def get(url: String)(implicit exec: Executor): Future[String] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[String]()

    f.addListener(new Runnable {
      def run(): Unit = {
        val response = f.get()
        if (response.getStatusCode / 100 < 4)
          p.success(response.getResponseBodyExcerpt(131072))
        else p.failure(BadStatus(response.getStatusCode))
      }
    }, exec)

    p.future
  }

  def shutdown(): Unit = client.close()
}

object WebClientTest extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  AsyncWebClient get "http://www.google.com" map println foreach (_ => AsyncWebClient.shutdown())
}
