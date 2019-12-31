package scala.scalanative.unsafe

import com.sun.jna.{FromNativeContext, Native, NativeLong, NativeMapped, Pointer, ToNativeContext, TypeConverter}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.scalanative.interop.JNI

trait Ptr[T] extends Pointer {
  def raw: Long
  def :=(value: T): Unit =  macro Ptr.MacroImpl.setPtrValue[T]
  def unary_!(): T = macro Ptr.MacroImpl.getPtrValue[T]
}


object Ptr {
  @inline final def apply[T](peer: Long): Ptr[T] = new Impl(peer)

  @inline final def alloc[T](size: Int): Ptr[T] = new Memory(new Array[Byte](size))

  implicit def ptrToCStruct[T <: CStruct](ptr: Ptr[T]): T = macro MacroImpl.ptrToCStruct[T]

  class Impl[T](val raw: Long) extends Pointer(raw) with Ptr[T]

  class Memory[T](buffer: Array[Byte], val raw: Long) extends Pointer(raw) with Ptr[T] {
    def this(buffer: Array[Byte]) = this(buffer, JNI.getByteArrayAddress(buffer))
  }

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
      new Impl(raw)
    }

    override def toNative(value: Any, context: ToNativeContext): AnyRef = value.asInstanceOf[Ptr[_]].raw.asInstanceOf[AnyRef]

    override def nativeType(): Class[_] = classOf[Long]
  }
  
}

