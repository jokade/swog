package scala.scalanative.interop

import scala.reflect.macros.whitebox

trait InteropMacroTools {
  val c: whitebox.Context

  import c.universe._

  /// Returns true if the RHS of the provided tree is 'extern'
  protected def isExtern(rhs: Tree): Boolean = rhs match {
    case Ident(TermName(id)) =>
      id == "extern"
    case Select(_,name) =>
      name.toString == "extern"
    case x =>
      false
  }

}
