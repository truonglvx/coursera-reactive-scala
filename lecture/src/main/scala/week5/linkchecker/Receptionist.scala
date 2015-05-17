package week5.linkchecker

import akka.actor._

object Receptionist {
  private case class Job(client: ActorRef, url: String)

  case class Get(url: String)

  case class Result(url: String, links: Set[String])

  case class Failed(url: String, reason: String)
}

class Receptionist extends Actor {
  import Receptionist._

  def controllerProps: Props = Props[Controller]

  override def supervisorStrategy = SupervisorStrategy.stoppingStrategy

  var reqNo = 0

  def receive: Receive = waiting

  val waiting: Receive = {
    // TODO: upon Get(url) start a traversal and become running
    case Get(url) =>
      context.become(runNext(Vector(Job(sender(), url))))
  }

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue.isEmpty) waiting
    else {
      val controller = context.actorOf(controllerProps, s"c$reqNo")
      context.watch(controller)
      controller ! Controller.Check(queue.head.url, 2)
      running(queue)
    }
  }

  def running(queue: Vector[Job]): Receive = {
    case Controller.Result(links) => // TODO: upon Controller.Result(links) ship that to client
      val job = queue.head
      job.client ! Result(job.url, links)
      context.stop(context.unwatch(sender()))
      // TODO: and run next job from queue if any
      context.become(runNext(queue.tail))
    case Terminated(_) =>
      val job = queue.head
      job.client ! Failed(job.url, "controller failed unexpectedly")
      context.become(runNext(queue.tail))
    case Get(url) => // TODO: upon Get(url) append that queue and keep running
      context.become(enqueueJob(queue, Job(sender(), url)))
  }

  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if (queue.size > 3) {
      sender ! Failed(job.url, "queue overflow")
      running(queue)
    } else running(queue :+ job)
  }
}
