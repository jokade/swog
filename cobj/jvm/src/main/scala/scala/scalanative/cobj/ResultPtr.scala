package scala.scalanative.cobj

import de.surfice.smacrotools.BlackboxMacroTools

import scala.reflect.macros.blackbox
import scala.scalanative.unsafe.{Ptr, Zone}
import scala.language.experimental.macros

final class ResultPtr[T](val ptr: Ptr[T]) extends CObject {
  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
  @inline def isDefined: Boolean = ??? //!ptr != null
  @inline def isEmpty: Boolean = !isDefined

  @inline def value: T = ???
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = ???
}
object ResultPtr {
  def alloc[T](implicit zone: Zone): ResultPtr[T] = macro Macros.alloc[T]
  def stackalloc[T]: ResultPtr[T] = macro Macros.stackalloc[T]

  private class Macros(val c: blackbox.Context) extends BlackboxMacroTools {
    import c.universe._

    val tCObject = weakTypeOf[CObject]

    def alloc[T: WeakTypeTag](zone: Tree): Tree = {
      val tpe = c.weakTypeOf[T]
      if(tpe <:< tCObject)
        q"new scalanative.cobj.ResultPtr(scalanative.unsafe.alloc[scalanative.unsafe.Ptr[Byte]].asInstanceOf[scalanative.unsafe.Ptr[$tpe]])"
      else
        q"""new scalanative.cobj.ResultPtr(scalanative.unsafe.alloc[$tpe])"""
    }

    def stackalloc[T: c.WeakTypeTag]: Tree = {
      val tpe = c.weakTypeOf[T]
      if(tpe <:< tCObject)
        q"""new scalanative.cobj.ResultPtr(scalanative.unsafe.stackalloc[scalanative.unsafe.Ptr[Byte]].asInstanceOf[scalanative.unsafe.Ptr[$tpe]])"""
      else
        q"""new scalanative.cobj.ResultPtr(scalanative.unsafe.stackalloc[$tpe])"""
    }
  }
}
