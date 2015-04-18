package hello.actors

import akka.routing.{RoundRobinPool, DefaultResizer}
import hello.utils.SpringExtension.SpringExtProvider
import akka.actor.{Props, ActorSystem, ActorRef}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by bebby on 4/10/2015.
 */
@Service
class ActorFactory @Autowired()(system: ActorSystem) {

  val numCPUs = Runtime.getRuntime.availableProcessors
  val resizer = DefaultResizer(
    lowerBound = numCPUs,
    upperBound = numCPUs * 2 + 1
  )

//  val weather = system.actorOf(SpringExtProvider.get(system).props("WeatherActor"), "weather")
//  val restClient = system.actorOf(SpringExtProvider.get(system).props("RestClientActor"), "restClient")
  val counter = system.actorOf(SpringExtProvider.get(system).props("CountingActor"), "counter")
//  val thingCrud = system.actorOf(SpringExtProvider.get(system).props("ThingActor"), "thingCrud")
//  val commentCrud = system.actorOf(SpringExtProvider.get(system).props("CommentActor"), "commentCrud")
//  val crawler = system.actorOf(SpringExtProvider.get(system).props("CrawlActor"), "crawler")
//  val nashorn = system.actorOf(SpringExtProvider.get(system).props("NashornActor"), "nashorn")

  def genWeatherActor(): ActorRef = {
    val weather = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("WeatherActor")))
    weather
  }

  def genGeoIpActor(): ActorRef = {
    val geoIp = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("GeoIpActor")))
    geoIp
  }

  def genRestClientActor: ActorRef = {
    val restClient = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("RestClientActor")))
    restClient
  }

  def genCountingActor: ActorRef = {
    counter
  }

  def genThingCrudActor: ActorRef = {
    val thingCrud = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("ThingActor")))
    thingCrud
  }

  def genCommentCrudActor: ActorRef = {
    val commentCrud = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("CommentActor")))
    commentCrud
  }

  def genCrawlActor: ActorRef = {
    val crawler = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("CrawlActor")))
    crawler
  }

  def genNashornActor: ActorRef = {
    val nashorn = system.actorOf(
      RoundRobinPool(numCPUs, Some(resizer)).props(SpringExtProvider.get(system).props("NashornActor")))
    nashorn
  }

  def genActor(props:Props): ActorRef = {
    val actor = system.actorOf(props)
    actor
  }
}
