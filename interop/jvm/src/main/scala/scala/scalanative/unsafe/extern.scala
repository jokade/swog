package scala.scalanative.unsafe

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.scalanative.interop.InteropMacroTools

final class extern extends StaticAnnotation

@compileTimeOnly("enable macro paradise to expand macro annotations")
// TODO: we need to define our macro annotation with another name than 'extern',
// since this symbol is already used as 'def', and thus would result in a conflicht,
// if we have a class with the same name that has a *body*.
// See https://github.com/scala-native/scala-native/issues/1719
final class external(libname: String = null) extends StaticAnnotation 
{
  def macroTransform(annottees: Any*): Any = macro external.Macro.impl
}
object external {

  protected[scalanative] class Macro(val c: whitebox.Context) extends MacroAnnotationHandler with InteropMacroTools {
    import c.universe._

    sealed trait JnaExternal {
      def name: String
      def jnaDef: Tree
      def scalaDef: Tree
    }
    case class JnaCall(name: String, jnaDef: Tree, scalaDef: Tree) extends JnaExternal
    case class JnaGlobal(name: String, jnaDef: Tree, scalaDef: Tree) extends JnaExternal
    type JnaExternals = Map[String,JnaExternal]
    implicit class JnaMacroData(data: Map[String,Any]) {
      type Data = Map[String, Any]

      def jnaExternals: JnaExternals = data.getOrElse("jnaExternals", Map()).asInstanceOf[JnaExternals]
      def addJnaExternals(externals: Iterable[JnaExternal]): Data = data.updated("jnaExternals",data.jnaExternals ++ externals.map(p => (p.name,p)))

      def jnaCalls: Iterable[JnaCall] = jnaExternals.collect{
        case (_,x: JnaCall) => x
      }

      def jnaGlobals: Iterable[JnaGlobal] = jnaExternals.collect{
        case (_,x: JnaGlobal) => x
      }

      def jnaLibraryName: String = data.getOrElse("jnaLibraryName","").asInstanceOf[String]
      def withJnaLibraryName(name: String): Data = data.updated("jnaLibraryName",name)

    }

    override def annotationName: String = "external"

    override def supportsClasses: CBool = false

    override def supportsTraits: CBool = false

    override def supportsObjects: CBool = true

    override def createCompanion: CBool = false


    override def analyze: Analysis = super.analyze andThen {
      case (obj: ObjectParts, data) =>
        val updData = (
          analyzeMainAnnotation(obj) _
            andThen analyzeBody(obj) _
          )(data)
        (obj, updData)
      case default => default
    }

    override def transform: Transformation = super.transform andThen {
      case obj: ObjectTransformData =>
        val jnaIFace = genJnaInterface()(obj.data)
        obj.updBody(genJnaTransformedBody(obj.modParts.body)(obj.data) ++ jnaIFace)
      case default => default
    }

    protected def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val externalLibname =
        extractAnnotationParameters(c.prefix.tree,Seq("libname")).apply("libname").flatMap(extractStringConstant)
      val linkName = findAnnotation(tpe.modifiers.annotations,"scala.scalanative.unsafe.link") match {
        case Some(annot) =>
          extractAnnotationParameters(annot,Seq("name")).apply("name").map(extractStringConstant).get.get
        case None =>
          tpe.fullName
      }

      data.withJnaLibraryName(externalLibname.getOrElse(linkName))
    }

    protected def analyzeBody(tpe: CommonParts)(data: Data): Data = {
      val externals = tpe.body.collect {
        case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          val jnaDef = genJnaDef(t)(data)
          val scalaDef = genJnaCall(t)(data)
          JnaCall(name.toString,jnaDef,scalaDef)
        case t@ValDef(mods,name,rettype,rhs) if isExtern(rhs) =>
          val jnaDef = genJnaGlobalDef(t)(data)
          val scalaDef = genJnaGlobal(t)(data)
          JnaGlobal(name.toString,jnaDef,scalaDef)
      }
      data.addJnaExternals(externals)
    }

    protected def genJnaTransformedBody(body: Seq[Tree])(implicit data: Data): Seq[Tree] = body.map{
      case scalaDef: DefDef if isExtern(scalaDef.rhs) =>
        data.jnaExternals(scalaDef.name.toString).scalaDef
      case scalaDef: ValDef if isExtern(scalaDef.rhs) =>
        data.jnaExternals(scalaDef.name.toString).scalaDef
      case x => x
    } ++ data.jnaGlobals.map(_.jnaDef)

    protected def genJnaInterface()(implicit data: Data): Seq[Tree] = {
      val jnaDefs = data.jnaCalls.map(_.jnaDef)
      Seq(q"""
          trait __IFace extends com.sun.jna.Library {
              ..$jnaDefs
          }""",
        q"""lazy val __inst: __IFace = scalanative.interop.JNA.loadInterface(${data.jnaLibraryName},classOf[__IFace])""")
    }

    protected def genJnaCall(scalaDef: DefDef)(implicit data: Data): DefDef = {
      val DefDef(mods,name,types,args,rettype,rhs) = scalaDef
      val params = args match {
        case Nil => Nil
        case List(params) => params map {
          case ValDef(_,name,_,_) => name
        }
        case x =>
          c.error(c.enclosingPosition, "extern methods with more than two parameter lists are not supported")
          ???
      }
      val call = q"__inst.$name(..$params)"
      DefDef(mods,name,types,args,rettype,call)
    }

    protected def genJnaDef(scalaDef: DefDef)(implicit data: Data): DefDef = {
      val DefDef(mods, name, types, args, rettype, rhs) = scalaDef
      DefDef(Modifiers(Flag.DEFERRED),name,types,args,rettype,q"")
    }

    protected def genJnaGlobalDef(scalaDef: ValDef)(implicit data: Data): Tree = {
      val name = genJnaPtrName(scalaDef)
      val tpt = tq"scalanative.unsafe.Ptr[${scalaDef.tpt}]"
      val libname = Literal(Constant(data.jnaLibraryName))
      val symbol = Literal(Constant(genExternalName(scalaDef)))
      val rhs = q"scalanative.interop.JNA.loadGlobalPtr[${scalaDef.tpt}]($libname,$symbol)"
      ValDef(Modifiers(Flag.LAZY),name,tpt,rhs)
    }

    protected def genJnaGlobal(valDef: ValDef)(implicit data: Data): DefDef = {
      val name = genJnaPtrName(valDef)
      DefDef(Modifiers(),valDef.name,Nil,Nil,valDef.tpt,q"!$name")
    }

    protected def genJnaPtrName(valDef: ValDef)(implicit data: Data): TermName =
      TermName("__g_"+valDef.name)

    protected def genExternalName(scalaDef: ValOrDefDef): String =
      findAnnotation(scalaDef.mods.annotations,"scala.scalanative.unsafe.name")
        .map{ annot =>
          extractAnnotationParameters(annot,Seq("name")).apply("name").flatMap(extractStringConstant).get
        }.getOrElse( scalaDef.name.toString )
  }

}
