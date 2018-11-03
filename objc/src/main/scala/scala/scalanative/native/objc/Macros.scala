package scala.scalanative.native.objc

import de.surfice.smacrotools.BlackboxMacroTools

import scala.reflect.macros.blackbox

private[this] class Macros(val c: blackbox.Context) extends BlackboxMacroTools with ObjCMacroTools {
  import c.universe._

  def superImpl(self: Tree)(f: Tree)(wrapper: Tree) = {
    val t = f match {
      case Function(_,Apply(f,args)) =>
        val method = f.symbol.asMethod
        val params = method.paramLists.head
        val callSuper = args.size match {
          case 0 => q"msgSendSuper0($self,sel)"
          case 1 => q"msgSendSuper1($self,sel,${args(0)})"
        }
        val result = wrapResult(callSuper,Some(method.returnType))
        q"""import scalanative.native.objc.runtime._
            import scalanative.native.objc.helper._
           val sel = sel_registerName(${cstring(genSelectorString(method))})
           $result
         """
    }
    t
  }

//  def superImpl(self: Tree)(f: Tree) = {
//    val t = f match {
//      case Function(_,Apply(f,args)) =>
//        val method = f.symbol.asMethod
//        val params = method.paramLists.head
//        val callSuper = args.size match {
//          case 0 => q"msgSendSuper0($self.cast[Ptr[Byte]],sel)"
//          case 1 => q"msgSendSuper1($self.cast[Ptr[Byte]],sel,${args(0)})"
//        }
//        q"""import scalanative.native.objc.runtime._
//            import scalanative.native.objc.helper._
//           val sel = sel_registerName(${cstring(genSelectorString(method))})
//           $callSuper.cast[${method.returnType}]
//         """
//    }
//    t
//  }
}
