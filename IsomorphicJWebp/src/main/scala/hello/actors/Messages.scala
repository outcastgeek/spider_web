package hello.actors

import akka.actor.ActorRef

import scala.collection.immutable.Map

/**
 * Created by bebby on 4/10/2015.
 */
object Messages {
  case object Inc
  case class Count(count:Int)
  case class Get(url_or_token:String)
  case class ReplyMap(data:Map[String, Object])
  case class Reply(data:Object)
  case class NoReply(origin:ActorRef)

  // Repository
  case class Find(id: java.lang.Long)
  case class All(asMap:Boolean = false)
}
