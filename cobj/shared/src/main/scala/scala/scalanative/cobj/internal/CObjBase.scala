package scala.scalanative.cobj.internal

import scala.scalanative.cobj.NamingConvention

abstract class CObjBase extends CommonHandler {
  import c.universe._
  
  def isMutable: Boolean = false
  override def annotationName = "scala.scalanative.cobj.CObj"
  override def supportsClasses: Boolean = true
  override def supportsTraits: Boolean = true
  override def supportsObjects: Boolean = true
  override def createCompanion: Boolean = true

  override protected def tpeDefaultParent = tpeCObject
  protected val annotationParamNames = Seq("prefix","namingConvention")

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
  
  


  protected def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
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


  protected def analyzeConstructor(cls: ClassParts)(data: Data): Data = {
    val companionStmts =
      if (cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT))
        List(genWrapperImplicit(cls.name, cls.tparams, cls.params))
      else
        Nil
    data
      //        .withConstructors( Seq( (genExternalName(data.externalPrefix,data.newSuffix,data.namingConvention),cls.params.asInstanceOf[List[ValDef]]) ) )
      .addCompanionStmts(companionStmts)
  }

  protected def genPrefixName(clsName: String): String =
    clsName.replaceAll("(.)([A-Z])","$1_$2").toLowerCase + "_"
}
