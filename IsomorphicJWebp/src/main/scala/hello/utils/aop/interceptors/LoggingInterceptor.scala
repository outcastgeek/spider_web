package hello.utils.aop.interceptors

import hello.utils.aop.{Invocation, Interceptor}
import hello.utils.aop.annotations.Loggable
import org.slf4j.LoggerFactory

/**
 * Created by outcastgeek on 4/12/15.
 */
trait LoggingInterceptor extends Interceptor {

  def logger = LoggerFactory.getLogger(super.getClass)

  val loggingPointcut = parser.parsePointcutExpression("execution(* *.foo(..))")

  abstract override def invoke(invocation: Invocation): AnyRef =
    if (matches(loggingPointcut , invocation)) {
      logger.info("=====> Enter: " + invocation.method.getName + " @ " + invocation.target.getClass.getName)
      val result = super.invoke(invocation)
      logger.info("=====> Exit: " + invocation.method.getName + " @ " + invocation.target.getClass.getName)
      result
    } else super.invoke(invocation)

//  val matchingLoggableAnnotation = classOf[Loggable]
//
//  abstract override def invoke(invocation: Invocation): AnyRef =
////    if (matches(matchingLoggableAnnotation, invocation)) {
////      logger.info("=====> REQUEST begins")
//      try {
//        logger.info("=====> REQUEST begins")
//        val result = super.invoke(invocation)
//        logger.info("=====> REQUEST completed")
//        result
//      } catch {
//        case e: Exception =>
//          logger.error("=====> REQUEST failed ")
//          "ERROR"
//      }
////    } else super.invoke(invocation)
}
