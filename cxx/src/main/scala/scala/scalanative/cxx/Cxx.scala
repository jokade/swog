package scala.scalanative.cxx

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.internal.CxxWrapperGen

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Cxx(namespace: String = null, classname: String = null, prefix: String = null, cxxType: String = null) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Cxx.Macro.impl
}

object Cxx {

  private[cxx] class Macro(val c: whitebox.Context) extends CommonHandler with CxxWrapperGen {

    override def annotationName = "scala.scalanative.native.cxx.Cxx"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = true
    override def supportsObjects: Boolean = true
    override def createCompanion: Boolean = true

    val annotationParamNames = Seq("namespace","classname","prefix","cxxType")

    import c.universe._

    private val tpeCxxObject = tq"$tCxxObject"
    override protected def tpeDefaultParent = tpeCxxObject


    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data) =>
        val updData = (
          analyzeMainAnnotation(cls) _
            andThen analyzeTypes(cls) _
            andThen analyzeConstructor(cls) _
            andThen analyzeBody(cls) _
          )(data)
        (cls, updData)
      case (obj: ObjectParts, data) =>
        val updData = (
          analyzeMainAnnotation(obj) _
            andThen analyzeBody(obj) _
          )(data)
        (obj, updData)
      case default => default
    }


    override def transform: Transformation = super.transform andThen {
      case cls: ClassTransformData =>
        cls
          .updBody(genTransformedTypeBody(cls))
          .addAnnotations(genCxxWrapperAnnot(cls.data))
          .updCtorParams(genTransformedCtorParams(cls))
          .updParents(genTransformedParents(cls))
      case obj: ObjectTransformData =>
        val transformedBody = genTransformedCompanionBody(obj) ++ obj.data.additionalCompanionStmts :+ genBindingsObject(obj.data)
        obj
          .updBody(transformedBody)
          .addAnnotations(genCxxSource(obj.data))
      case default => default
    }


    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val externalPrefix = annotParams("prefix") match {
        case Some(prefix) => extractStringConstant(prefix).get
        case None => genPrefixName(tpe)
      }
      val namespace = annotParams("namespace") match {
        case Some(ns) => extractStringConstant(ns).get.trim match {
          case "" => None
          case x => Some(x)
        }
        case _ => None
      }
      val classname = annotParams("classname") match {
        case Some(cn) => extractStringConstant(cn).get.trim match {
          case "" => None
          case x => Some(x)
        }
        case _ => None
      }
      val cxxType = annotParams("cxxType") match {
        case Some(t) => Some(extractStringConstant(t).get)
        case None => None
      }
      val updData = data
        .withExternalPrefix(externalPrefix)
        .withCxxNamespace(namespace)
        .withCxxClassName(classname)
        .withCxxType(cxxType)
      analyzeCxxAnnotation(tpe)(updData)
    }

    private def analyzeConstructor(cls: ClassParts)(data: Data): Data = {
      val companionStmts =
        if (cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT))
          List(genWrapperImplicit(cls.name, cls.tparams, cls.params))
        else
          Nil
      data
        .withAdditionalCompanionStmts(data.additionalCompanionStmts ++ companionStmts)
    }

    private def genPrefixName(tpe: CommonParts): String =
      tpe.fullName.replaceAll("\\.","_") + "_"


    override def analyzeBody(tpe: CommonParts)(data: Data): Data =
      ( super.analyzeBody(tpe) _ andThen analyzeCxxBody(tpe) _ )(data)


  }
}
