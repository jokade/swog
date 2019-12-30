package scala.scalanative.unsafe

import com.sun.jna.{FromNativeContext, Native, NativeLong, NativeMapped, Pointer, ToNativeContext, TypeConverter}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

class Ptr[T](_peer: Long) extends Pointer(_peer) {
  def this() = this(0)

//  override def nativeType(): Class[_] = classOf[Long]
//  override def fromNative(nativeValue: Any, context: FromNativeContext): AnyRef = {
//    peer = nativeValue.asInstanceOf[Long]
//    this
//  }
//  override def toNative: AnyRef = peer.asInstanceOf[AnyRef]

  def raw: Long = peer

//  @inline def raw: RawPtr = this

  def :=(value: T): Unit =  macro Ptr.MacroImpl.setPtrValue[T]
  def unary_!(): T = macro Ptr.MacroImpl.getPtrValue[T]

}


object Ptr {
  @inline final def apply[T](peer: Long): Ptr[T] = new Ptr(peer)

  implicit def ptrToCStruct[T <: CStruct](ptr: Ptr[T]): T = macro MacroImpl.ptrToCStruct[T]
  
  private class MacroImpl(val c: blackbox.Context) extends MacroTools {
    import c.universe._
    
    def setPtrValue[T: c.WeakTypeTag](value: c.Tree): c.Tree = {
      genSetValue(c.prefix,weakTypeOf[T],0,value)
    }

    def getPtrValue[T: c.WeakTypeTag](): c.Tree = genGetValue(c.prefix,weakTypeOf[T],0)

  }

  object PtrConverter extends TypeConverter {
    override def fromNative(nativeValue: Any, context: FromNativeContext): AnyRef = {
      val raw = nativeValue.asInstanceOf[Long]
      new Ptr(raw)
    }

    override def toNative(value: Any, context: ToNativeContext): AnyRef = value.asInstanceOf[Ptr[_]].raw.asInstanceOf[AnyRef]

    override def nativeType(): Class[_] = classOf[Long]
  }
  
}

