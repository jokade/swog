package scala.scalanative.cobj

import de.surfice.smacrotools.BlackboxMacroTools

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.scalanative.unsafe._

trait ResultValue[T<:CObject] extends CObject {
  def __ptr: Ptr[Byte]
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = macro ResultValue.Macros.wrappedThis[T]
  @inline final def :=(f: Function1[ResultValue[T],_]): Unit = f(this)
}
object ResultValue {
  final class Impl[T<:CObject](val __ptr: Ptr[Byte])(implicit tag: Tag[T]) extends ResultValue[T] {
    @inline override def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = macro ResultValue.Macros.wrappedValueImpl[T]
  }

  def apply[T<:CObject](obj: T)(implicit tag: Tag[T]): ResultValue[T] = new Impl[T](obj.__ptr)

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
          q"new scalanative.cobj.ResultValue.Impl[$tpe](scalanative.unsafe.alloc[Byte](${tpe.typeSymbol.companion}.__sizeof)($tag,$zone))"
        else
        ???
      tree
    }

    def stackallocImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val tree =
        if(tpe <:< tCObject)
          q"new scalanative.cobj.ResultValue.Impl[$tpe](scalanative.unsafe.stackalloc[Byte](${tpe.typeSymbol.companion}.__sizeof))"
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

    def wrappedThis[T: WeakTypeTag](wrapper: Tree) =
      c.Expr(q"this")

   }

}


