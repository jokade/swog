package scala.scalanative.cobj

import de.surfice.smacrotools.BlackboxMacroTools

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.scalanative.unsafe._

final class Result[T](val ptr: Ptr[T])(implicit tag: Tag[T]) extends CObject {
  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
  @inline def isDefined: Boolean = !ptr != null
  @inline def isEmpty: Boolean = !isDefined
//  @inline def valuePtr: Ptr[Byte] = !ptr
  @inline def value: T = macro Result.Macros.valueImpl[T]
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = macro Result.Macros.wrappedValueImpl[T]
//  @inline def value_=(v: T): Unit = macro Result.Macros.setValueImpl[T]
//  @inline def option: Option[T] = macro Result.Macros.optionImpl[T]
//  @inline def clear(): Unit = !ptr = null
}
object Result {

  def alloc[T](implicit tag: Tag[T], zone: Zone): Result[T] = macro Macros.allocImpl[T]
  def stackalloc[T]: Result[T] = macro Macros.stackallocImpl[T]

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
          q"new scalanative.cobj.Result(scalanative.unsafe.alloc[Ptr[Byte]]($tag.asInstanceOf[scalanative.unsafe.Tag[scalanative.unsafe.Ptr[Byte]]],$zone).asInstanceOf[scalanative.unsafe.Ptr[$tpe]])"
        else
          q"new scalanative.cobj.Result(scalanative.unsafe.alloc[$tpe]($tag,$zone))"
      tree
    }

    def stackallocImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val tree =
        if(tpe <:< tCObject)
          q"new scalanative.cobj.Result(scalanative.unsafe.stackalloc[Ptr[Byte]].asInstanceOf[Ptr[$tpe]])"
        else
          q"new scalanative.cobj.Result(scalanative.unsafe.stackalloc[$tpe])"
      tree
    }

    def valueImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal || tpe <:< tPtr || tpe <:< tCStruct )
          q"!$self.ptr"
        else {
          c.error(c.enclosingPosition,"use method 'wrappedValue' to access CObject results")
          ???
        }
      c.Expr(tree)
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
             $wrapper.wrap(!($self.ptr.asInstanceOf[Ptr[Ptr[Byte]]]))
           """
      c.Expr(tree)
    }

    def setValueImpl[T: WeakTypeTag](v: Tree) = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal || tpe <:< tPtr )
          q"""!$self.ptr = $v.cast[scalanative.native.Ptr[Byte]]"""
        else
          q"""!$self.ptr = $v.__ptr"""
      tree
    }

    def optionImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal)
          q"""
               Some($self.valuePtr.cast[$tpe])
             """
        else
          q"""
             if($self.isDefined) Some(new $tpe($self.valuePtr))
             else None
           """
      //        println(tree)
      tree
    }

  }
}

