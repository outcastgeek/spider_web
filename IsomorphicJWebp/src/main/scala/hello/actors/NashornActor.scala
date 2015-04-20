package hello.actors

import javax.script.ScriptException

import akka.actor.Actor
import akka.event.Logging
import hello.actors.Messages.NoReply
import hello.actors.NashornActor.RenderComponent
import hello.utils.JsUtil
import jdk.nashorn.api.scripting.NashornScriptEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import scala.collection.immutable.List
import scala.concurrent.duration._

/**
 * Created by bebby on 4/16/2015.
 */
@Component(value = "NashornActor")
@Scope(value = "prototype")
class NashornActor @Autowired() (nashorn:NashornScriptEngine) extends Actor {

  val log = Logging(context.system, this)

  val errMsg = "Cannot execute the Rendering"

  def receive = {
    case RenderComponent(data, js_paths) =>
      implicit val ec = context.dispatcher
      val timeout = context.system.scheduler.scheduleOnce(2 seconds, self, NoReply(sender))
      try {
        js_paths map {js =>
          nashorn.eval(JsUtil.read(js))
        }
        val rendered_html = nashorn.invokeFunction("renderServer", data)
        sender ! rendered_html
        timeout.cancel()
      } catch {
        case ex @ (_:ScriptException | _:NoSuchMethodException) =>
          timeout.cancel()
          self ! NoReply(sender)
          log.error("ERROR::::", ex)
      }
    case NoReply(origin) =>
      log.info(errMsg)
      origin ! errMsg
      context become receive
    case _ =>
      log.info("Received Unknown Message")
  }
}

object  NashornActor {
  case class RenderComponent(data:Object, js_paths:List[String])
//  case class RenderComponent(data:Object, js_path:String, js_paths:String*)
}

