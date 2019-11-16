package scala.scalanative.unsafe

import com.sun.jna.{FromNativeContext, NativeMapped, Pointer}
import scala.reflect.runtime.universe._

final class Ptr[T] (protected[scalanative] var peer: Long) extends NativeMapped {
  def this() = this(0)
  override def fromNative(nativeValue: Any, context: FromNativeContext): AnyRef = {
    peer = nativeValue.asInstanceOf[Long]
    this
  }
  override def toNative: AnyRef = peer.asInstanceOf[AnyRef]
  override def nativeType(): Class[_] = classOf[Long]

  def unary_!(): T = ??? //macro ...
}
object Ptr {
  implicit def ptrToCStruct[T <: CStruct](ptr: Ptr[T]): T = ???
}

