package hello.actors

import akka.actor.Actor
import akka.event.Logging
import hello.actors.Messages.{Reply, NoReply, ReplyMap}

import scala.collection.immutable.Map
import scala.concurrent.duration._

/**
 * Created by outcastgeek on 4/11/15.
 */
trait ReqRepActor extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Service is Currently Busy"

  implicit val ec = context.dispatcher

  lazy val timeout = context.system.scheduler.scheduleOnce(duration, self, NoReply)

  def duration:FiniteDuration

  def req():Unit

  def reply(data:Map[String, Object]):Unit = {}

  def reply(data:Object):Unit = {}

  override def preStart: Unit = {
    req()
  }

  def receive: Receive = {
    case Reply(data) =>
      timeout.cancel()
      reply(data)
      context stop self
    case ReplyMap(data) =>
      timeout.cancel()
      reply(data)
      context stop self
    case NoReply =>
      reply(Map("error" -> errMsg))
      context stop self
    case _ =>
      log.info("Received Unknown Message")
  }
}
