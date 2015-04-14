package hello.actors

import akka.actor.{Actor, ActorRef, Cancellable}
import akka.event.Logging
import hello.actors.Messages.{Get, NoReply, Reply}
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

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

  override def receive = {
    case Get(ip_or_hostname) =>
      val url = s"http://$ip_or_hostname"
      restClient ! Get(url)
      implicit val ec = context.dispatcher
      val timeout = context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(sender))
      context become extractLinks(sender, timeout)
    case _ =>
      log.info("Received Unknown Message")
  }

  def extractLinks(actorRef: ActorRef, timeout: Cancellable): Receive = {
    case Reply(rawPageData) =>
      timeout.cancel()
      val pageData = Jsoup.parse(rawPageData.getOrElse(RestClientActor.replyKey, "").asInstanceOf[String])
      val pageLinks = pageData.select("a[href]").toArray().toList
      pageLinks foreach { link =>
        log.info(link.toString)
      }
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


