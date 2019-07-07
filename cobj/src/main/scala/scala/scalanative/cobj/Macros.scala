package scala.scalanative.cobj

import de.surfice.smacrotools.BlackboxMacroTools
import scala.reflect.macros.blackbox

private[cobj] class Macros(val c: blackbox.Context) extends BlackboxMacroTools {
  import c.universe._

  private def wrapperTree(tpe: Type): Tree =
    q"${tpe.typeSymbol.name.toTermName}.__wrapper"

  def defaultWrapperImpl[T:WeakTypeTag] = {
    val tpe = weakTypeOf[T]
    val tree =  wrapperTree(tpe)
    tree
  }

  def singletonWrapperImpl[T:WeakTypeTag] = {
    val wrapper = wrapperTree(weakTypeOf[T])
    val tree = q"new scalanative.cobj.CObjWrapper.SingletonFactory($wrapper)"
    tree
  }
}
