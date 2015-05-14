package week5.actor

import akka.actor.{Actor, Props}

class CounterMain extends Actor {
  var counter = context.actorOf(Props[Counter], "counter")

  counter ! "incr"
  counter ! "incr"
  counter ! "incr"
  counter ! "get"

  def receive: Receive = {
    case count: Int =>
      println(s"count was $count")
      context.stop(self)
  }
}
