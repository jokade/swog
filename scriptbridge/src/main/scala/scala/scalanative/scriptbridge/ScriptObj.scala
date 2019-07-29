package scala.scalanative.scriptbridge

import de.surfice.smacrotools.{ExtendableMacro, ExtendableMacroAnnotationHandler, MacroAnnotationHandler}

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.scalanative.scriptbridge.internal.ScriptBridgeHandler

@compileTimeOnly("enable macro paradise to expand macro annotations")
class ScriptObj extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ScriptObj.Macro.impl
}

object ScriptObj {

  private[scriptbridge] class Macro(val c: whitebox.Context) extends ExtendableMacroAnnotationHandler with ScriptBridgeHandler {

    import c.universe._

    override def annotationName: String = "scala.scalanative.scriptbridge.ScriptObj"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = true
    override def supportsObjects: Boolean = true
    override def createCompanion: Boolean = true

    override def language: String = ???

    override def extensionClasses: Seq[String] = Seq("lua.scriptbridge.LuaScriptBridge")

    override def analyze: Analysis = analyzeData andThen super.analyze

    private def analyzeData: Analysis = {
      case (obj: ObjectParts, data: Data) =>
        val updData = sbAnalyzeObject(obj)(data)
        (obj,updData)
      case default => default
    }


  }
}
