package scala.scalanative.scriptbridge.internal

import de.surfice.smacrotools.MacroAnnotationHandler

trait ScriptBridgeHandler extends MacroAnnotationHandler {
  import c.universe._

  override def annotationName: String = ???
  override def supportsClasses: Boolean = ???
  override def supportsTraits: Boolean = ???
  override def supportsObjects: Boolean = ???
  override def createCompanion: Boolean = ???

  case class Constructor(name: TypeName, params: Seq[ValDef])

  implicit class ScriptBridgeData(data: Map[String,Any]) {
    def sbScalaType: Option[TypeName] = data.getOrElse("sbScalaType",None).asInstanceOf[Option[TypeName]]
    def withSbScalaType(tpe: TypeName): Data = data.updated("sbScalaType",Some(tpe))

    /// all functions that should be exported by the bridge handler
    def sbExportFunctions: Seq[DefDef] = data.getOrElse("sbExportFunctions",Nil).asInstanceOf[Seq[DefDef]]
    def withSbExportFunctions(functions: Seq[DefDef]): Data = data.updated("sbExportFunctions",functions)

    /// all methods that should be exported by the bridge handler
    def sbExportMethods: Seq[DefDef] = data.getOrElse("sbExportMethods",Nil).asInstanceOf[Seq[DefDef]]
    def withSbExportMethods(functions: Seq[DefDef]): Data = data.updated("sbExportMethods",functions)

    def sbExportProperties: Seq[ValDef] = data.getOrElse("sbExportProperties",Nil).asInstanceOf[Seq[ValDef]]
    def withSbExportProperties(props: Seq[ValDef]): Data = data.updated("sbExportProperties",props)

    def sbModuleName: String = data("sbModuleName").asInstanceOf[String]
    def withSbModuleName(name: String): Data = data.updated("sbModuleName",name)

    def sbConstructor: Option[Constructor] = data.get("sbConstructor").map(_.asInstanceOf[Constructor])
    def withSbConstructor(constr: Constructor): Data = data.updated("sbConstructor",constr)
  }

  def language: String

  protected def sbAnalyzeAnnottee(p: CommonParts)(data: Data): Data = p match {
    case cls: ClassParts =>
      (sbAnalyzeCommon(p) _
        andThen sbAnalyzeConstructor(cls) _
        andThen sbAnalyzeMethods(cls) _
        andThen sbAnalyzeCompanion(cls.companion) _
        )(data)
    case obj: ObjectParts =>
      (sbAnalyzeCommon(p) _
        andThen sbAnalyzeFunctions(obj) _
        )(data)
    case _ => data
  }

  private def sbAnalyzeConstructor(cls: ClassParts)(data: Data): Data =
    data
      .withSbConstructor(Constructor(cls.name,cls.params.collect{
        case v: ValDef => v
      }))
      .withSbScalaType(cls.name)

  private def sbAnalyzeCompanion(obj: Option[ObjectParts])(data: Data): Data =
    if(obj.isDefined)
      sbAnalyzeFunctions(obj.get)(data)
    else
      data

  private def sbAnalyzeFunctions(tpe: CommonParts)(data: Data): Data =
    data.withSbExportFunctions(tpe.body.collect{
      case m: DefDef if shouldExport(m) => m
    })

  private def sbAnalyzeMethods(tpe: CommonParts)(data: Data): Data =
    data
      .withSbExportMethods(tpe.body.collect{
        case m: DefDef if shouldExport(m) => m
      })
      .withSbExportProperties(tpe.body.collect{
        case p: ValDef if shouldExport(p) => p
      })


  private def sbAnalyzeCommon(tpe: CommonParts)(data: Data): Data =
    data.withSbModuleName(tpe.fullName)

  private def shouldExport(m: ValOrDefDef): Boolean = isPublic(m)

  private def isPublic(m: ValOrDefDef): Boolean =
    ! ( m.mods.hasFlag(Flag.PROTECTED) || m.mods.hasFlag(Flag.PRIVATE) )
}
