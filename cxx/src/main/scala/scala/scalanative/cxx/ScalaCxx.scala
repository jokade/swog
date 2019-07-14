package scala.scalanative.cxx

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.internal.CxxWrapperGen

@compileTimeOnly("enable macro paradise to expand macro annotations")
class ScalaCxx(name: String = null) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ScalaCxx.Macro.impl
}

object ScalaCxx {
  private[cxx] class Macro(val c: whitebox.Context) extends CommonHandler with CxxWrapperGen {
    override def annotationName = "scala.scalanative.native.cxx.ScalaCxx"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = false
    override def supportsObjects: Boolean = false
    override def createCompanion: Boolean = true

    val annotationParamNames = Seq("name")

    import c.universe._

    private val tpeCxxObject = tq"$tCxxObject"
    override protected def tpeDefaultParent = tpeCxxObject

    implicit class ScalaCxxData(data: Map[String,Any]) {
      type Data = Map[String, Any]

      def cxxName: String = data.getOrElse("scalaCxxName","").asInstanceOf[String]
      def withCxxName(name: String): Data = data.updated("scalaCxxName",name)
    }

    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data) =>
        val updData = (
          analyzeMainAnnotation(cls) _
          )(data)
        (cls,updData)
      case (obj: ObjectParts, data) =>
        (obj,data)
      case default => default
    }


    override def transform: Transformation = super.transform andThen {
      case cls: ClassTransformData =>
        val externalSource = genCxxWrapper(genScalaCxxClass(cls.data))
        cls
          .addAnnotations(externalSource)
      case default => default
    }

    private def analyzeMainAnnotation(cls: ClassParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val cxxName = annotParams("name") match {
        case Some(name) => extractStringConstant(name).get
        case None => cls.fullName.replaceAll("\\.","::")
      }
      data
        .withCxxName(cxxName)
    }

    private def genScalaCxxClass(data: Data): String = {
      s"""class ${data.cxxName} {
         |};
       """.stripMargin
    }

  }
}
