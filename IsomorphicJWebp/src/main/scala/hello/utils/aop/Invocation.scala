package hello.utils.aop

import java.lang.reflect.Method

/**
 * Created by outcastgeek on 4/12/15.
 */
case class Invocation(val method: Method, val args: Array[AnyRef], val target: AnyRef) {
  def invoke: AnyRef = method.invoke(target, args:_*)

  override def toString: String = s"Invocation [method: ${method.getName}, args: $args, target: $target}]"

  override def hashCode(): Int = super.hashCode()

  override def equals(that: Any): Boolean = super.equals(that)
}
