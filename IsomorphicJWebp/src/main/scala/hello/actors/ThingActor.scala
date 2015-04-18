package hello.actors

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.Messages._
import hello.actors.ThingActor.{ByName, Save}
import hello.models.Thing
import hello.models.repositories.ThingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._
import scala.collection.immutable.Map
import scala.concurrent.duration._

/**
 * Created by outcastgeek on 4/14/15.
 */
@Component(value = "ThingActor")
@Scope(value = "prototype")
class ThingActor @Autowired() (thingRepository: ThingRepository) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Thing Repository is Currently Busy"

  def startTimer(origin: ActorRef):Cancellable = {
    implicit val ec = context.dispatcher
    context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(origin))
  }

  def receive = {
    case Save(thing, asMap) =>
      val timeout = startTimer(sender)
      thingRepository.save(thing)
      log.info(s"Persisted $thing")
      if (asMap)
        sender ! ReplyMap(Map(ThingActor.replyKey -> thing))
      else
        sender ! Reply(thing)
      timeout.cancel()
    case Find(id) =>
      val timeout = startTimer(sender)
      val thing = thingRepository.findOne(id)
      sender ! ReplyMap(Map(ThingActor.replyKey -> thing))
      timeout.cancel()
    case ByName(name) =>
      val timeout = startTimer(sender)
      val  thingList = thingRepository.findByName(name, new PageRequest(0, 19))
      log.info(s"All Things named $name=>")
      thingList.toList.foreach(thing => log.info(s"$thing"))
      sender ! ReplyMap(Map(ThingActor.replyKey -> thingList))
      timeout.cancel()
    case All(asMap) =>
      val timeout = startTimer(sender)
      val thingList = thingRepository.findAll()
      thingList.toList.foreach(thing => log.info(s"$thing"))
//      val thingList = thingRepository.findAll().toList
      log.info(s"All Things=>")
//      thingList.foreach(thing => log.info(s"$thing"))
//      thingList match {
//        case head::tail => sender ! Reply(Map(ThingActor.replyKey -> head))
//        case Nil => sender ! NoReply
//      }
      if (asMap) {
        sender ! ReplyMap(Map(ThingActor.replyKey -> thingList))
      } else {
        sender ! Reply(thingList)
      }
      timeout.cancel()
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! ReplyMap(Map("error" -> errMsg))
    case _ =>
      log.info("Received Unknown Message")
  }
}

object ThingActor extends CanReply {
  def replyKey: String = "thing_data"

  case class Save(thing: Thing, asMap: Boolean = false)
  case class ByName(name: String)
}
