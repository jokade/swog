package scala.scalanative.cobj

import de.surfice.smacrotools.BlackboxMacroTools

import scala.reflect.macros.blackbox
import scalanative.unsafe._
import scala.language.experimental.macros
import scala.scalanative.cobj.runtime.CObjObject

final class Out[T](val ptr: Ptr[Ptr[Byte]]) extends CObjObject {
  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
//  @inline def ptr: Ptr[Ptr[Byte]] = __ptr.cast[Ptr[Ptr[Byte]]]
  @inline def isDefined: Boolean = !ptr != null
  @inline def isEmpty: Boolean = !isDefined
  @inline def valuePtr: Ptr[Byte] = !ptr
  @inline def value: T = macro Out.Macros.valueImpl[T]
  @inline def value_=(v: T): Unit = macro Out.Macros.setValueImpl[T]
  @inline def option: Option[T] = macro Out.Macros.optionImpl[T]
  @inline def clear(): Unit = !ptr = null
}
object Out {

  def alloc[T](implicit zone: Zone): Out[T] = new Out[T](scalanative.unsafe.alloc[Ptr[Byte]])

  class Macros(val c: blackbox.Context) extends BlackboxMacroTools {
    import c.universe._

    val tAnyVal = weakTypeOf[AnyVal]
    val tPtr = weakTypeOf[Ptr[_]]

    def allocImpl[T: WeakTypeTag](zone: Tree) = {
      val tpe = weakTypeOf[T]
      val tree =
        q"""
              val ptr = scalanative.native.alloc[Ptr[Byte]]
              !ptr = null
              ptr.cast[scalanative.native.cobj.Out[$tpe]]
           """
      tree
    }

    def valueImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal || tpe <:< tPtr )
          q"""
               $self.valuePtr.cast[$tpe]
             """
        else
          q"""
             if($self.isDefined) new $tpe($self.valuePtr)
             else null
           """
      //        println(tree)
      tree
    }

    def setValueImpl[T: WeakTypeTag](v: Tree) = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal || tpe <:< tPtr)
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

