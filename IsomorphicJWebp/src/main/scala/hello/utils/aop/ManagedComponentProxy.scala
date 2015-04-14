package hello.utils.aop

import java.lang.reflect.{Method, InvocationHandler}

/**
 * Created by outcastgeek on 4/12/15.
 */
class ManagedComponentProxy(val target: AnyRef) extends InvocationHandler with Interceptor {
  def invoke(proxy: AnyRef, m: Method, args: Array[AnyRef]): AnyRef = invoke(Invocation(m, args, target))
  def invoke(invocation: Invocation): AnyRef = invocation.invoke
}
