package hello.utils

import akka.actor.{Actor, IndirectActorProducer}
import org.springframework.context.ApplicationContext

/**
 * Created by outcastgeek on 4/8/15.
 */

/**
 * An actor producer that lets Spring create the Actor instances.
 */
class SpringActorProducer(applicationContext: ApplicationContext, actorBeanName:String)
  extends IndirectActorProducer {

  override def produce(): Actor = {
    applicationContext.getBean(actorBeanName).asInstanceOf[Actor]
  }

  override def actorClass: Class[_ <: Actor] = {
    applicationContext.getType(actorBeanName).asInstanceOf[Class[_ <: Actor]]
  }
}
