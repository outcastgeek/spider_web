package hello.actors

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.Messages._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration._
import scala.xml.XML
import scala.collection.immutable.Map

/**
 * Created by outcastgeek on 4/9/15.
 */
@Component(value = "WeatherActor")
@Scope(value = "prototype")
class WeatherActor @Autowired() (actorFactory: ActorFactory) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Weather Service is Currently Busy"

  val (counter, restClient) = (
    actorFactory.genCountingActor,
    actorFactory.genRestClientActor
    )

  def receive = {
    case Get(station) =>
      val url = s"http://w1.weather.gov/xml/current_obs/${station.toUpperCase()}.xml"
      restClient ! Get(url)
      implicit val ec = context.dispatcher
      val timeout = context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(sender))
      context become getCurrentWeather(sender, station, timeout)
    case _ =>
      log.info("Received Unknown Message")
  }

  def getCurrentWeather(origin:ActorRef, station:String, timeout: Cancellable):Receive = {
    case ReplyMap(rawWeatherData) =>
      timeout.cancel()
      val weatherData = XML.loadString(
        rawWeatherData.getOrElse(RestClientActor.replyKey, "").asInstanceOf[String]) \\ "current_observation"
      val (location, time, currentTemp) =(
        weatherData \\ "location" text,
        weatherData \\ "observation_time" text,
        weatherData \\ "temperature_string" text
        )
      val currentWeather = s"The current temperature for $location ($station) is $currentTemp, $time"
      counter ! Inc
      context become incCount(origin, currentWeather)
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! ReplyMap(Map("error" -> errMsg))
      context become receive
  }

  def incCount(origin:ActorRef, currentWeatherData:String):Receive = {
    case Count(currentCount) =>
      val currentWeather = s"$currentWeatherData, #$currentCount"
      origin ! ReplyMap(Map(WeatherActor.replyKey -> currentWeather))
      context become receive
  }
}

object WeatherActor extends CanReply {
  override def replyKey: String = "weather"
}


