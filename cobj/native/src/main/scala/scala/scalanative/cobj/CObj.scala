package scala.scalanative.cobj

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.scalanative.cobj.internal.{CObjBase, CommonHandler}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class CObj(prefix: String = null, namingConvention: NamingConvention.Value = NamingConvention.SnakeCase) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CObj.Macro.impl
}

object CObj {

  class CObjWrapper extends StaticAnnotation
  
  private[cobj] class Macro(val c: whitebox.Context) extends CObjBase {

    import c.universe._

    protected val cobjWrapperAnnotation = q"new scalanative.cobj.CObj.CObjWrapper"

    override def transform: Transformation = super.transform andThen {
      /* transform class */
      case cls: ClassTransformData =>
        val transformedBody = genTransformedTypeBody(cls)
        cls
          .updBody(transformedBody)
          .addAnnotations(cobjWrapperAnnotation)
          .updCtorParams(genTransformedCtorParams(cls))
          .updParents(genTransformedParents(cls))
      /* transform trait */
      case trt: TraitTransformData =>
        val transformedBody = genTransformedTypeBody(trt)
        trt
          .updBody(transformedBody)
          .addAnnotations(cobjWrapperAnnotation)
          .updParents(genTransformedParents(trt))
      /* transform companion object */
      case obj: ObjectTransformData =>
        val transformedBody = genTransformedCompanionBody(obj) ++ obj.data.additionalCompanionStmts :+ genBindingsObject(obj.data)
        obj.updBody(transformedBody)
      case default => default
    }

/*
    private def genWrapperImplicit(tpe: TypeName, tparams: Seq[Tree]): Tree =
      tparams.size match {
        case 0 =>
          q"""implicit object __wrapper extends scalanative.cobj.CObjectWrapper[$tpe] {
            def wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
            def unwrap(value: $tpe): scalanative.unsafe.Ptr[Byte] = value.__ptr
          }
          """
        case 1 =>
          q"""implicit object __wrapper extends scalanative.cobj.CObjectWrapper[$tpe[_]] {
            def wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
            def unwrap(value: $tpe[_]): scalanative.unsafe.Ptr[Byte] = value.__ptr
          }
          """
        case 2 =>
          q"""implicit object __wrapper extends scalanative.cobj.CObjectWrapper[$tpe[_,_]] {
            def wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
            def unwrap(value: $tpe[_,_]): scalanative.unsafe.Ptr[Byte] = value.__ptr
          }
          """
      }
*/


    private def isAbstract(t: TypeTransformData[TypeParts]): Boolean = t match {
      case cls : ClassTransformData => t.modParts.modifiers.hasFlag(Flag.ABSTRACT)
      case _ => false
    }

  }
}
