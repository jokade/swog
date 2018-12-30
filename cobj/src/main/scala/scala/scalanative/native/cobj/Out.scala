package scala.scalanative.native.cobj

import de.surfice.smacrotools.BlackboxMacroTools

import scala.reflect.macros.blackbox
import scalanative.native._
import scala.language.experimental.macros

final class Out[T](ptr: Ptr[Ptr[Byte]]) {
  @inline def isDefined: Boolean = !ptr != null
  @inline def isEmpty: Boolean = !isDefined
  @inline def valuePtr: Ptr[Byte] = !ptr
  @inline def value: Option[T] = macro Out.Macros.valueImpl[T]
  @inline def clear(): Unit = !ptr = null
}
object Out {

  def alloc[T](implicit zone: Zone): Out[T] = new Out[T](scalanative.native.alloc[Ptr[Byte]])

  class Macros(val c: blackbox.Context) extends BlackboxMacroTools {
    import c.universe._

    val tAnyVal = weakTypeOf[AnyVal]

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

