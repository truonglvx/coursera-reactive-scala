package week5.linkchecker

import java.util.concurrent.Executor

import akka.actor.{Status, Actor}
import akka.pattern.pipe
import org.jsoup.Jsoup
import week5.linkchecker.Getter.Done

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class Getter(url: String, depth: Int) extends Actor {

  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  def client: WebClient = AsyncWebClient

  client get url pipeTo self

  def receive: Receive = {
    case body: String =>
      for (link <- findLinks(body))
        context.parent ! Controller.Check(link, depth)
      stop()
    case _: Status.Failure => stop()
  }

  def findLinks(body: String): Iterator[String] = {
    val document = Jsoup.parse(body, url)
    val links = document.select("a[href]")

    for {
      link <- links.iterator().asScala
    } yield link.absUrl("href")
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}

object Getter {
  case object Done
}