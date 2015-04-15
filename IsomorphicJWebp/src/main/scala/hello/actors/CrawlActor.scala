package hello.actors

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.Messages.{Get, NoReply, Reply}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.collection.immutable.Map
import scala.concurrent.duration._

/**
 * Created by bebby on 4/14/2015.
 */
@Component(value = "CrawlActor")
@Scope(value = "prototype")
class CrawlActor @Autowired() (actorFactory: ActorFactory) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Weather Service is Currently Busy"

  val restClient = actorFactory.genRestClientActor

  val URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".r

  override def receive = {
    case Get(ip_or_hostname) =>
      val url = s"http://$ip_or_hostname"
      restClient ! Get(url)
      implicit val ec = context.dispatcher
      val timeout = context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(sender))
      context become extractLinks(sender, url, timeout)
    case _ =>
      log.info("Received Unknown Message")
  }

  def extractLinks(origin: ActorRef, url: String, timeout: Cancellable): Receive = {
    case Reply(rawPageData) =>
      timeout.cancel()
      val pageData = Jsoup.parse(rawPageData.getOrElse(RestClientActor.replyKey, "").asInstanceOf[String])
      val pageLinks = pageData.select("a[href]").toArray().toList.asInstanceOf[List[Element]]
      val collectedURLS = pageLinks map { link =>
        val href = link.attr("href")
        href match {
          case URL_REGEX(href) =>
            val wfURL = href
            log.info(wfURL)
            wfURL
          case _ =>
            val pURL = s"$url$href"
            log.info(pURL)
            pURL
        }
      }
      origin ! Reply(Map(CrawlActor.replyKey -> collectedURLS.toArray))
      context become receive
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! Reply(Map("error" -> errMsg))
      context become receive
  }
}

object CrawlActor extends CanReply {
  override def replyKey: String = "crawl_data"
}


