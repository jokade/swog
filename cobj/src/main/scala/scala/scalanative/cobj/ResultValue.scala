package scala.scalanative.cobj

import de.surfice.smacrotools.BlackboxMacroTools

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.scalanative.unsafe._

final class ResultValue[T<:CObject](val __ptr: Ptr[Byte])(implicit tag: Tag[T]) extends CObject {
//  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
//  @inline def isDefined: Boolean = !ptr != null
//  @inline def isEmpty: Boolean = !isDefined
//  @inline def valuePtr: Ptr[Byte] = !ptr
//  @inline def value: T = macro Result.Macros.valueImpl[T]
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = macro ResultValue.Macros.wrappedValueImpl[T]
//  @inline def value_=(v: T): Unit = macro Result.Macros.setValueImpl[T]
//  @inline def option: Option[T] = macro Result.Macros.optionImpl[T]
//  @inline def clear(): Unit = !ptr = null
}
object ResultValue {

  def apply[T<:CObject](obj: T)(implicit tag: Tag[T]): ResultValue[T] = new ResultValue[T](obj.__ptr)

  def alloc[T](implicit tag: Tag[Byte], zone: Zone): ResultValue[T] = macro Macros.allocImpl[T]
  def stackalloc[T]: ResultValue[T] = macro Macros.stackallocImpl[T]

  class Macros(val c: blackbox.Context) extends BlackboxMacroTools {
    import c.universe._

    val tAnyVal  = weakTypeOf[AnyVal]
    val tPtr     = weakTypeOf[Ptr[_]]
    val tCStruct = weakTypeOf[CStruct]
    val tCObject = weakTypeOf[CObject]

    def allocImpl[T: WeakTypeTag](tag: Tree, zone: Tree) = {
      val tpe = weakTypeOf[T]
      val tree =
        if(tpe <:< tCObject)
          q"new scalanative.cobj.ResultValue[$tpe](scalanative.unsafe.alloc[Byte](${tpe.typeSymbol.companion}.__sizeof)($tag,$zone))"
        else
        ???
      tree
    }

    def stackallocImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val tree =
        if(tpe <:< tCObject)
          q"new scalanative.cobj.ResultValue[$tpe](scalanative.unsafe.stackalloc[Byte](${tpe.typeSymbol.companion}.__sizeof))"
        else
        ???
      tree
    }


    def wrappedValueImpl[T: WeakTypeTag](wrapper: Tree) = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal || tpe <:< tPtr || tpe <:< tCStruct ) {
          c.error(c.enclosingPosition,s"cannot wrap value of type $tpe; use method 'value' instead")
          ???
        }
        else
          q"""
             $wrapper.wrap($self.__ptr)
           """
      c.Expr(tree)
    }

   }

}


