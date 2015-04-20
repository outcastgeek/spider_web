package hello.actors

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import hello.actors.Messages.{Get, NoReply}
import hello.utils.ScalaObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Created by bebby on 4/13/2015.
 */
@Component(value = "GeoIpActor")
@Scope(value = "prototype")
class GeoIpActor @Autowired() (actorFactory: ActorFactory, scalaObjectMapper: ScalaObjectMapper) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Geolocation Service is Currently Busy"

  val restClient = actorFactory.genRestClientActor

  override def receive: Receive = {
    case Get(ip_or_hostname) =>
      implicit val ec = context.dispatcher
      implicit val delay = Timeout(2 seconds)
      val timeout = context.system.scheduler.scheduleOnce(delay.duration, self, NoReply(sender))
      val origin = sender
      val url = s"http://freegeoip.net/json/$ip_or_hostname"
      restClient ? Get(url) andThen {
        case Success(rawGeoLocationData) =>
          timeout.cancel()
          val geoLocationData = scalaObjectMapper.readValue(
            rawGeoLocationData.asInstanceOf[String], classOf[java.util.HashMap[String, String]])
          origin ! geoLocationData
        case Failure(_) =>
          origin ! errMsg
          log.error(errMsg)
      }
    case _ =>
      log.info("Received Unknown Message")
  }
}


