package scala.scalanative.unsafe

import scala.reflect.macros.blackbox

protected[unsafe] class MacroImpl(val c: blackbox.Context) extends MacroTools {
  import c.universe._

  // TODO: provide real stack allocation instead of heap allocation
  def stackalloc[T: c.WeakTypeTag]: c.Tree = {
    val size = computeFieldSize(weakTypeOf[T].dealias)
    q"""scalanative.unsafe.Ptr.alloc($size)"""
  }

  def alloc[T: c.WeakTypeTag]: c.Tree = {
    val size = computeFieldSize(weakTypeOf[T].dealias)
    q"""scalanative.unsafe.Ptr.alloc($size)"""
  }
}

