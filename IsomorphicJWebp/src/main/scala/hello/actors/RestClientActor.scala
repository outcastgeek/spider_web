package hello.actors

import akka.actor.Actor
import akka.event.Logging
import hello.actors.Messages.{Get, Reply}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.web.client.{HttpClientErrorException, RestTemplate}

import scala.collection.immutable.Map

/**
 * Created by bebby on 4/10/2015.
 */

@Component(value = "RestClientActor")
@Scope(value = "prototype")
class RestClientActor extends Actor {

  val log = Logging(context.system, this)

  @Autowired()
  var restTemplate:RestTemplate = _

  def receive = {
    case Get(url) =>
      try {
        val response = restTemplate.getForEntity(url, classOf[String])
        val strResp = response.getBody
        sender ! Reply(Map("response" -> strResp))
      } catch {
        case ex:HttpClientErrorException =>
          log.error(s"ERROR:::: $ex")
          context stop self
      }
    case _ =>
      log.info("Received Unknown Message")
  }
}

object RestClientActor extends CanReply {
  override def replyKey = "response"
}


