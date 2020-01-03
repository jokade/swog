package scala.scalanative.cxx

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.internal.CxxWrapperGen
import scala.language.experimental.macros

//@compileTimeOnly("enable macro paradise to expand macro annotations")
class CxxTemplate(namespace: String = null, classname: String = null) extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro CxxTemplate.Macro.impl
}

object CxxTemplate {

  private[cxx] class Macro(val c: whitebox.Context) extends CommonHandler with CxxWrapperGen {

    override def annotationName = "scala.scalanative.native.cxx.CxxTemplate"

    override def supportsClasses: Boolean = false
    override def supportsTraits: Boolean = true
    override def supportsObjects: Boolean = false
    override def createCompanion: Boolean = false

    val annotationParamNames = Seq("namespace", "classname")

    import c.universe._

    private val tpeCxxObject = tq"$tCxxObject"
    override protected def tpeDefaultParent = tpeCxxObject

    override def analyze: Analysis = super.analyze andThen {
      case (trt: TraitParts, data) =>
        val updData = (
          analyzeMainAnnotation(trt) _
            andThen analyzeTypes(trt) _
            andThen analyzeBody(trt) _
          )(data)
        (trt,updData)
      case default => default
    }

    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)

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
      val updData = data
        .withCxxNamespace(namespace)
        .withCxxClassName(classname)
      analyzeCxxAnnotation(tpe)(updData)
    }
  }

}
