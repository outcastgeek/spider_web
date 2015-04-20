package hello.actors

import akka.actor.Actor
import akka.event.Logging
import hello.actors.Messages.Inc
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by outcastgeek on 4/9/15.
 */

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 * instance for use of this bean.
 */
@Component(value = "CountingActor")
@Scope(value = "prototype")
class CountingActor extends Actor{

  val log = Logging(context.system, this)

  var count = 0

  def receive = {
    case Inc =>
      count = count + 1
      sender ! count
    case _ =>
      log.info("Received Unknown Message")
  }
}


