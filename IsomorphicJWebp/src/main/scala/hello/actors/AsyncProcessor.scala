package hello.actors

import akka.actor.{ActorRef, Props}
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.servlet.ModelAndView

import scala.concurrent.duration.FiniteDuration
import scala.collection.immutable.Map
import scala.collection.JavaConversions._

/**
 * Created by outcastgeek on 4/13/15.
 */
object AsyncProcessor {

  def run(actorRef: ActorRef, msg:AnyRef)
         (implicit
          actorFactory:ActorFactory,
          delay: FiniteDuration,
          response: DeferredResult[ModelAndView],
          mv: ModelAndView) = {

    actorFactory.genActor(Props(
      new ReqRepActor {

        override def duration: FiniteDuration = delay

        override def req(): Unit =
          actorRef ! msg

        override def reply(data: Map[String, Object]): Unit = {
          mv.addAllObjects(data)
          response.setResult(mv)
        }
      }
    ))
  }
}

