package hello.actors

import javax.persistence.NonUniqueResultException

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.Messages.{NoReply, Reply}
import hello.actors.ThingActor.{All, ByName, Find, Save}
import hello.models.Thing
import hello.models.repositories.ThingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.collection.immutable.Map
import scala.concurrent.duration._
import scala.collection.JavaConversions._

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
    case Save(thing) =>
      val timeout = startTimer(sender)
      try {
        thingRepository.save(thing)
        log.info(s"Persisted $thing")
      } catch {
        case ex:NonUniqueResultException =>
          log.error(s"Could not persist $thing, ${ex.getMessage}")
      }
      timeout.cancel()
    case Find(id) =>
      val timeout = startTimer(sender)
      val thing = thingRepository.findOne(id)

      sender ! Reply(Map(ThingActor.replyKey -> thing))
      timeout.cancel()
    case ByName(name) =>
      val timeout = startTimer(sender)
      val  thing = thingRepository.getByName(name)
      log.info(s"$thing")
      sender ! Reply(Map(ThingActor.replyKey -> thing))
      timeout.cancel()
    case All =>
      val timeout = startTimer(sender)
      val thingList = thingRepository.findAll()
      log.info(s"All Things=>")
      thingList.foreach(thing => log.info("$thing"))
      sender ! Reply(Map(ThingActor.replyKey -> thingList))
      timeout.cancel()
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! Reply(Map("error" -> errMsg))
    case _ =>
      log.info("Received Unknown Message")
  }
}

object ThingActor extends CanReply {
  def replyKey: String = "thing_data"

  case class Save(thing: Thing)
  case class Find(id: java.lang.Long)
  case class ByName(name: String)
  case object All
}
