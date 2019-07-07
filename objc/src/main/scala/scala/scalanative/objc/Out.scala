package scala.scalanative.objc

import de.surfice.smacrotools.BlackboxMacroTools

import scala.reflect.macros.blackbox
import scala.language.experimental.macros
import scalanative.unsafe._

final class Out[T](ptr: Ptr[Ptr[Byte]]) extends ObjCObject {
  !ptr = null
  @inline override def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
  @inline def isDefined: Boolean = !ptr != null
  @inline def isEmpty: Boolean = !isDefined
  @inline def valuePtr: Ptr[Byte] = !ptr
  @inline def value: Option[T] = macro Out.Macros.valueImpl[T]
  @inline def clear(): Unit = !ptr = null
}
object Out {

  def apply[T,R](f: (Out[T]) => R): R = macro Macros.applyImpl[T,R]
  def alloc[T](implicit zone: Zone): Out[T] = new Out(scalanative.unsafe.alloc[Ptr[Byte]])

  class Macros(val c: blackbox.Context) extends BlackboxMacroTools with ObjCMacroTools {
    import c.universe._

    def applyImpl[T: WeakTypeTag, R: WeakTypeTag](f: Tree) = {
      val tree =
        q"""
           val out = new Out[${weakTypeOf[T]}](scalanative.native.stackalloc[scalanative.native.Ptr[Byte]])
           $f(out)
         """

      tree
    }

    def valueImpl[T: WeakTypeTag] = {
      val tpe = weakTypeOf[T]
      val self = c.prefix
      val tree =
        if(tpe <:< tAnyVal)
          q"""
               Some($self.valuePtr.asInstanceOf[$tpe])
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
