package hello.utils

import akka.actor.{Props, ExtendedActorSystem, AbstractExtensionId, Extension}
import hello.utils.SpringExtension.SpringExt
import org.springframework.context.ApplicationContext

/**
 * Created by outcastgeek on 4/8/15.
 */

/**
 * An Akka Extension to provide access to Spring managed Actor Beans.
 */
class SpringExtension extends AbstractExtensionId[SpringExtension.SpringExt] {

  /**
   * The identifier used to access the SpringExtension.
   */
  override def createExtension(system: ExtendedActorSystem): SpringExt = new SpringExt
//  override def createExtension(system: ExtendedActorSystem): SpringExt = ???
}

object SpringExtension {

  /**
   * The identifier used to access the SpringExtension.
   */
  val SpringExtProvider:SpringExtension = new SpringExtension

  /**
   * The Extension implementation.
   */
  class SpringExt extends Extension {

    var applicationContext:ApplicationContext = _

    /**
     * Used to initialize the Spring application context for the extension.
     * @param applicationContext
     */
    def initialize(applicationContext: ApplicationContext) = {
      this.applicationContext = applicationContext
    }

    /**
     * Create a Props for the specified actorBeanName using the
     * SpringActorProducer class.
     *
     * @param actorBeanName  The name of the actor bean to create Props for
     * @return a Props that will create the named actor bean using Spring
     */
    def props(actorBeanName:String): Props = {
      Props.create(classOf[SpringActorProducer], applicationContext, actorBeanName)
    }
  }
}
