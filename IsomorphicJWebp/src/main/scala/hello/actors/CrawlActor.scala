package hello.actors

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import hello.actors.Messages.{Get, NoReply}
import hello.models.{Datum, Thing, Tag}
import hello.models.repositories.ThingRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.collection.immutable.List
import scala.collection.JavaConversions._

/**
 * Created by bebby on 4/14/2015.
 */
@Component(value = "CrawlActor")
@Scope(value = "prototype")
class CrawlActor@Autowired()
(actorFactory: ActorFactory) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "The Weather Service is Currently Busy"

  val (restClient, thingCrud) = (
    actorFactory.genRestClientActor,
    actorFactory.genThingCrudActor
    )

  val URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".r

  override def receive = {
    case Get(ip_or_hostname) =>
      implicit val ec = context.dispatcher
      implicit val delay = Timeout(2 seconds)
      val timeout = context.system.scheduler.scheduleOnce(delay.duration, self, NoReply(sender))
      val origin = sender
      val url = s"http://$ip_or_hostname"
      restClient ? Get(url) andThen {
        case Success(rawPageData) =>
          timeout.cancel()
          val pageData = Jsoup.parse(rawPageData.asInstanceOf[String])
          val pageLinks = pageData.select("a[href]").toArray().toList.asInstanceOf[List[Element]]
          val collectedURLS = (pageLinks map { link =>
            val href = link.attr("href")
            href match {
              case URL_REGEX(href) =>
                val wfURL = if (
                  href.equalsIgnoreCase("http") ||
                    href.equalsIgnoreCase("https")
                ) url
                else href
                //            log.info(wfURL)
                wfURL
              case _ =>
                val pURL = if (href.equals("/")) url else s"$url$href"
                //            log.info(pURL)
                pURL
            }
          }).toSeq.filter(!_.equalsIgnoreCase(url)).filter(!_.contains("/tag/"))

          val pagingLinks = collectedURLS.filter(_.contains("/page/"))
          val contentLinks = (collectedURLS diff pagingLinks) union (pagingLinks diff collectedURLS)

          contentLinks.par.map { contentLink =>
            restClient ? Get(contentLink) andThen {
              case Success(rawPageData) =>
                val thing = new Thing
                thing.name = "blog"
                val pageData = Jsoup.parse(rawPageData.asInstanceOf[String])
                val tags = (pageData.select("a.tag[href]").toArray().toList.asInstanceOf[List[Element]].map { tagEl =>
                  val tag = new Tag
                  tag.name = tagEl.text()
//                  tag.thing = thing
                  tag
                }).toSet[Tag]
//                thing.tags = tags.toList
                val contentDatum = new Datum()
                contentDatum.key = "proverb"
                contentDatum.value = pageData.select("blockquote").first().text()
//                contentDatum.thing = thing
                thing.data = List(contentDatum)
                thingCrud ? ThingActor.Save(thing) andThen {
                  case Success(thing) =>
                    log.info(s"Persisted: $thing")
                  case Failure(_) =>
                    log.error("Could not persist a thing")
                }
              case Failure(_) =>
                log.error(s"Could not process: $contentLink")
            }
          }

          origin ! contentLinks.toArray[String]
        case Failure(_) =>
          log.error(errMsg)
      }
    case _ =>
      log.info("Received Unknown Message")
  }
}


