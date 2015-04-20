package hello.actors

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import hello.actors.Messages._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.xml.XML

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
      implicit val ec = context.dispatcher
      implicit val delay = Timeout(2 seconds)
      val timeout = context.system.scheduler.scheduleOnce(delay.duration, self, NoReply(sender))
      val origin = sender
      val url = s"http://w1.weather.gov/xml/current_obs/${station.toUpperCase()}.xml"
      restClient ? Get(url) andThen {
        case Success(rawWeatherData) =>
          val weatherData = XML.loadString(
            rawWeatherData.asInstanceOf[String]) \\ "current_observation"
          val (location, time, currentTemp) =(
            weatherData \\ "location" text,
            weatherData \\ "observation_time" text,
            weatherData \\ "temperature_string" text
            )
          val currentWeatherData = s"The current temperature for $location ($station) is $currentTemp, $time"
          counter ? Inc andThen {
            case Success(currentCount) =>
              timeout.cancel()
              val currentWeather = s"$currentWeatherData, #$currentCount"
              origin ! currentWeather
            case Failure(_) =>
              log.error(errMsg)
          }
        case Failure(_) =>
          log.error(errMsg)
      }
    case _ =>
      log.info("Received Unknown Message")
  }
}


