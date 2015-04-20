package hello.actors

import akka.actor.ActorRef

import scala.collection.immutable.Map

/**
 * Created by bebby on 4/10/2015.
 */
object Messages {
  case object Inc
  case class Get(url_or_token:String)
  case class NoReply(origin:ActorRef)

  // Repository
  case class Find(id: java.lang.Long)
  case object All
}
