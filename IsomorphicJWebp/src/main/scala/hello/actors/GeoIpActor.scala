package hello.actors

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.Messages.{Get, NoReply, Reply}
import hello.utils.ScalaObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.collection.immutable.Map
import scala.concurrent.duration._

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
      val url = s"http://freegeoip.net/json/$ip_or_hostname"
      restClient ! Get(url)
      implicit val ec = context.dispatcher
      val timeout = context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(sender))
      context become getGeolocation(sender, ip_or_hostname, timeout)
    case _ =>
      log.info("Received Unknown Message")
  }

  def getGeolocation(origin:ActorRef, ip_or_hostname:String, timeout:Cancellable): Receive = {
    case Reply(rawGeoLocationData) =>
      timeout.cancel()
      val geoLocationData = scalaObjectMapper.readValue(
        rawGeoLocationData.getOrElse(RestClientActor.replyKey, "").asInstanceOf[String], classOf[java.util.HashMap[String, String]])
      origin ! Reply(Map("ip_or_hostname" -> ip_or_hostname, GeoIpActor.replyKey -> geoLocationData))
      context become receive
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! Reply(Map("error" -> errMsg))
      context become receive
  }
}

object GeoIpActor extends CanReply {
  override def replyKey: String = "geo_location"
}

