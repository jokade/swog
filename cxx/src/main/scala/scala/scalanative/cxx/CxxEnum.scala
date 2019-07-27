package scala.scalanative.cxx

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class CxxEnum(name: String = null) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CxxEnum.Macro.impl
}

object CxxEnum {

  private[cxx] class Macro(val c: whitebox.Context) extends MacroAnnotationHandler {
    override def annotationName = "scala.scalanative.native.cxx.CxxEnum"
    override def supportsClasses: Boolean = false
    override def supportsTraits: Boolean = false
    override def supportsObjects: Boolean = true
    override def createCompanion: Boolean = false

    val annotationParamNames = Seq("name")

    import c.universe._

    implicit class CxxEnumMacroData(data: Map[String,Any]) {
      type Data = Map[String, Any]

      def enumName: String = data.getOrElse("cxxEnumName","").asInstanceOf[String]
      def withEnumName(s: String): Data = data.updated("cxxEnumName",s)
    }


    override def analyze: Analysis = super.analyze andThen {
      case (t, data) =>
        (t, analyzeMainAnnotation(t)(data))
    }

    override def transform: Transformation = super.transform andThen {
      case obj: ObjectTransformData =>
        obj
          .updBody(genTransformedBody(obj))
      case default => default
    }

    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val enumName = annotParams("name") match {
        case Some(name) => extractStringConstant(name).get
        case None => tpe.fullName.replaceAll("\\.","::")
      }
      data.
        withEnumName(enumName)
    }

    private def genTransformedBody(obj: ObjectTransformData): Seq[Tree] = {
      val nameArg = Literal(Constant(obj.data.enumName))
      obj.modParts.body ++ Seq(
        q"""
           @scalanative.unsafe.name($nameArg) class Value(value: Int) extends super.Value(value) {
              def |(or: Value): Value = new Value(value | or.value)
              def &(and: Value): Value = new Value(value & and.value)
           }
         """,
        q"override def Value(value: Int): Value = new Value(value)"
      )
    }
  }
}
