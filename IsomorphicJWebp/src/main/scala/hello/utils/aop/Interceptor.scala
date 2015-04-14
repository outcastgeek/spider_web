package hello.utils.aop

import java.lang.annotation.Annotation

import org.aspectj.weaver.tools.{PointcutExpression, PointcutParser}

/**
 * Created by outcastgeek on 4/12/15.
 */
trait Interceptor {
  protected val parser = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution

  protected def matches(pointcut: PointcutExpression, invocation: Invocation): Boolean = {
    pointcut.matchesMethodExecution(invocation.method).alwaysMatches ||
    invocation.target.getClass.getDeclaredMethods.exists(pointcut.matchesAdviceExecution(_).alwaysMatches) ||
    false
  }

  protected def matches(annotationClass: Class[T] forSome {type T <: Annotation}, invocation: Invocation): Boolean = {
    invocation.method.isAnnotationPresent(annotationClass) ||
    invocation.target.getClass.isAnnotationPresent(annotationClass) ||
    false
  }

  def invoke(invocation: Invocation): AnyRef
}
