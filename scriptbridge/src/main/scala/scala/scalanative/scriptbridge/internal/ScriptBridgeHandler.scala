package scala.scalanative.scriptbridge.internal

import de.surfice.smacrotools.MacroAnnotationHandler

trait ScriptBridgeHandler extends MacroAnnotationHandler {
  import c.universe._

  override def annotationName: String = ???
  override def supportsClasses: Boolean = ???
  override def supportsTraits: Boolean = ???
  override def supportsObjects: Boolean = ???
  override def createCompanion: Boolean = ???

  implicit class ScriptBridgeData(data: Map[String,Any]) {
    /// all functions that should be exported by the bridge handler
    def sbExportFunctions: Seq[DefDef] = data.getOrElse("sbExportFunctions",Nil).asInstanceOf[Seq[DefDef]]
    def withSbExportFunctions(functions: Seq[DefDef]): Data = data.updated("sbExportFunctions",functions)

    def sbModuleName: String = data("sbModuleName").asInstanceOf[String]
    def withSbModuleName(name: String): Data = data.updated("sbModuleName",name)
  }

  def language: String

  protected def sbAnalyzeObject(obj: ObjectParts)(data: Data): Data =
    (sbAnalyzeAnnottee(obj) _
      andThen sbAnalyzeFunctions(obj) _
      )(data)

  private def sbAnalyzeFunctions(tpe: CommonParts)(data: Data): Data =
    data.withSbExportFunctions(tpe.body.collect{
      case m: DefDef if shouldExport(m) => m
    })

  private def sbAnalyzeAnnottee(tpe: CommonParts)(data: Data): Data =
    data.withSbModuleName(tpe.fullName)

  private def shouldExport(m: DefDef): Boolean = isPublic(m)

  private def isPublic(m: DefDef): Boolean =
    ! ( m.mods.hasFlag(Flag.PROTECTED) || m.mods.hasFlag(Flag.PRIVATE) )
}
