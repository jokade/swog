// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 1)
package scala.scalanative.unsafe

import com.sun.jna.{FromNativeContext, NativeMapped, Pointer}

import scala.reflect.runtime.universe._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.scalanative.runtime.RawPtr

final class Ptr[T] (protected[scalanative] var rawptr: RawPtr) extends NativeMapped {
  def this() = this(null)
  override def fromNative(nativeValue: Any, context: FromNativeContext): AnyRef = {
    rawptr = nativeValue.asInstanceOf[RawPtr]
    this
  }
  override def toNative: AnyRef = rawptr.asInstanceOf[AnyRef]
  override def nativeType(): Class[_] = classOf[RawPtr]

  def :=(value: T): Unit =  macro Ptr.MacroImpl.setPtrValue[T]
  def unary_!(): T = macro Ptr.MacroImpl.getPtrValue[T]
}
object Ptr {
  implicit def ptrToCStruct[T <: CStruct](ptr: Ptr[T]): T = macro MacroImpl.ptrToCStruct[T]
  
  private class MacroImpl(val c: blackbox.Context) extends MacroTools {
    import c.universe._
    
    def setPtrValue[T: c.WeakTypeTag](value: c.Tree): c.Tree = {
      genSetValue(c.prefix,weakTypeOf[T],0,value)
    }

    def getPtrValue[T: c.WeakTypeTag](): c.Tree = genGetValue(c.prefix,weakTypeOf[T],0)

  }
  
}

