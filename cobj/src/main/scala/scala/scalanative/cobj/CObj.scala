package scala.scalanative.cobj

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.{TypecheckException, whitebox}
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.unsafe.Ptr

@compileTimeOnly("enable macro paradise to expand macro annotations")
class CObj(prefix: String = null, namingConvention: NamingConvention.Value = NamingConvention.SnakeCase) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CObj.Macro.impl
}

object CObj {

  class CObjWrapper extends StaticAnnotation

  private[cobj] class Macro(val c: whitebox.Context) extends CommonHandler {

    def isMutable: Boolean = false
    override def annotationName = "scala.scalanative.native.cobj.CObj"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = true
    override def supportsObjects: Boolean = true
    override def createCompanion: Boolean = true

    import c.universe._

    private val tCObjWrapperAnnotation = weakTypeOf[CObjWrapper]
    private val cobjWrapperAnnotation = q"new scalanative.cobj.CObj.CObjWrapper"
    override protected def tpeDefaultParent = tpeCObject

    private val annotationParamNames = Seq("prefix","namingConvention")

    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data) =>
        val updData = (
          analyzeMainAnnotation(cls) _
            andThen analyzeTypes(cls) _
            andThen analyzeConstructor(cls) _
            andThen analyzeBody(cls) _
          )(data)
        (cls, updData)
      case (trt: TraitParts, data) =>
        val updData = (
          analyzeMainAnnotation(trt) _
            andThen analyzeTypes(trt) _
            andThen analyzeBody(trt) _
          )(data)
        (trt, updData)
      case (obj: ObjectParts, data) =>
        val updData = (
          analyzeMainAnnotation(obj) _
            andThen analyzeBody(obj) _
          )(data)
        (obj, updData)
      case default => default
    }

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

    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val externalPrefix = annotParams("prefix") match {
        case Some(prefix) => extractStringConstant(prefix).get
        case None => genPrefixName(tpe.nameString)
      }
      val namingConvention = annotParams("namingConvention") match {
        case Some(Select(_,name)) => NamingConvention.withName(name.toString)
        case None => NamingConvention.SnakeCase
      }
      data.withExternalPrefix(externalPrefix).withNamingConvention(namingConvention)
    }


    private def analyzeConstructor(cls: ClassParts)(data: Data): Data = {
      val companionStmts =
        if (cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT))
          List(genWrapperImplicit(cls.name, cls.tparams, cls.params))
        else
          Nil
      data
//        .withConstructors( Seq( (genExternalName(data.externalPrefix,data.newSuffix,data.namingConvention),cls.params.asInstanceOf[List[ValDef]]) ) )
        .withAdditionalCompanionStmts(data.additionalCompanionStmts ++ companionStmts)
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

    private def genPrefixName(clsName: String): String =
      clsName.replaceAll("(.)([A-Z])","$1_$2").toLowerCase + "_"


    private def isAbstract(t: TypeTransformData[TypeParts]): Boolean = t match {
      case cls : ClassTransformData => t.modParts.modifiers.hasFlag(Flag.ABSTRACT)
      case _ => false
    }

  }
}
