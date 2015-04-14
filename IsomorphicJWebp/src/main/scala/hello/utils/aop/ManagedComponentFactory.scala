package hello.utils.aop

import java.lang.reflect.Proxy

/**
 * Created by outcastgeek on 4/12/15.
 */
object ManagedComponentFactory {
  def createComponent[T](intf: Class[T] forSome {type T}, proxy: ManagedComponentProxy): T =
    Proxy.newProxyInstance(
      proxy.target.getClass.getClassLoader,
      Array(intf),
      proxy).asInstanceOf[T]
}
