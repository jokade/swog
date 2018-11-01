package scala.scalanative.native.objc


import de.surfice.smacrotools.MacroAnnotationHandler

import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.{TypecheckException, whitebox}
import scala.scalanative.native.objc.runtime.ObjCObject

@compileTimeOnly("enable macro paradise to expand macro annotations")
class ObjC() extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ObjC.ObjCMacro.impl
}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class ObjCClass() extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ObjC.ObjCClassMacro.impl
}

class selector(name: String) extends StaticAnnotation

object ObjC {

  private[native] class ObjCMacro(val c: whitebox.Context) extends BaseMacro {
    override def isObjCClass: Boolean = false
  }
  private[native] class ObjCClassMacro(val c: whitebox.Context) extends BaseMacro {
    override def isObjCClass: Boolean = true
  }

  // marks that a class wraps an ObjC-object in the __ptr var
  class Wrapper extends StaticAnnotation

  private[native] abstract class BaseMacro
    extends MacroAnnotationHandler
    with ObjCMacroTools {

    import c.universe._

    def isObjCClass: Boolean

    override val annotationName: String = "scala.scalanative.native.ObjC"
    override val supportsClasses: Boolean = true
    override val supportsTraits: Boolean = true
    override val supportsObjects: Boolean = true
    override val createCompanion: Boolean = true

    private val tObjCObject = c.weakTypeOf[ObjCObject]
    private val wrapperAnnot = q"new scalanative.native.objc.ObjC.Wrapper"

    override def analyze: Analysis = super.analyze andThen {
      case (cls: TypeParts, data) =>
        // collect selectors to be emitted into companion object body
        val selectors = cls.body.collect {
          case DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
            genSelector(name, args)
        }
        (cls, data.selectors = data.selectors ++ selectors)
      case default => default
    }

    override def transform: Transformation = super.transform andThen {
      /* transform class */
      case cls: ClassTransformData =>
        val ctorParams = transformCtorParams(cls.modParts.params)

        val parents = transformParents(cls.modParts.parents)

        val annots =
          if(isObjCClass) cls.modParts.modifiers.annotations
          else cls.modParts.modifiers.annotations :+ wrapperAnnot

        val transformedBody =
          (if(cls.data.replaceClassBody.isDefined)
            cls.data.replaceClassBody.get
          else
            cls.modParts.body)
            .map {
              case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs)  =>
                val selectorTerm = q"${cls.modParts.name.toTermName}.${genSelector(name, args)._2}"
                val call =
                  if(isObjCClass)
                    genCall(q"__cls", selectorTerm, args, rettype)
                  else
                    genCall(q"this.__ptr", selectorTerm, args, rettype)
                DefDef(mods, name, types, args, rettype, call)
              case x => x
            }

        cls
          .updAnnotations(annots)
          .updCtorParams(ctorParams)
          .updParents(parents)
          .updBody(ccastImport +: transformedBody)
          .updCtorMods(Modifiers(Flag.PROTECTED))  // ensure that the class can't be instatiated using new

      /* transform companion object */
      case obj: ObjectTransformData =>
        // val containing the class reference
        val objcCls =
          q"""lazy val __cls = {
              import scalanative.native._
              ..${obj.data.objcClassInits}
              objc.runtime.objc_getClass(CQuote(StringContext(${obj.modParts.name.toString})).c() )
              }"""
        // collect selector definitions from class
        val clsSelectors = obj.data.selectors
        // collect selector definitions and statements (transformed ObjC-calls and other statements)
        // from companion
        val (objSelectors, objStmts) = obj.modParts.body
          .map {
            case t@DefDef(mods, name, types, args, rettype, Ident(TermName("extern"))) =>
              val sel = genSelector(name, args)
              val call = genCall(clsTarget, sel._2, args, rettype) //q"objc.objc_msgSend(_cls,$selectorVal,${paramNames(t)})"
              (Some(sel), DefDef(mods, name, types, args, rettype, call))
            case stmt => (None, stmt)
          }.unzip
        // create selector definitions
        val selectorDefs = (clsSelectors ++ objSelectors.collect { case Some(sel) => sel })
          .toMap
          .map(p => genSelectorDef(p._1, p._2))
        // new body = (transformed) statements ++ selector definitions
        val transformedBody = (Seq(ccastImport) ++ selectorDefs :+ objcCls) ++ objStmts ++ obj.data.additionalCompanionStmts
        obj.updBody(transformedBody)
      case default => default
    }

    private def transformCtorParams(params: Seq[Tree]): Seq[Tree] =
      if(isObjCClass) Nil
      else Seq(q"private[this] val __ptr: Ptr[Byte]")

    private def transformParents(parents: Seq[Tree]): Seq[Tree] =
      if(isObjCClass) parents
      else parents map (p => (p,getType(p))) map {
        case (tree,tpe) if tpe =:= tObjCObject => tree
        case (tree,tpe) if tpe <:< tObjCObject => q"$tree(__ptr)"
        case (tree,_) => tree
      }

    private def isExtern(rhs: Tree): Boolean = rhs match {
      case Ident(TermName(id)) =>
        id == "extern"
      case Select(_,name) =>
        name.toString == "extern"
      case x =>
        false
    }


    private def genCall(target: TermName, selectorVal: TermName, argsList: List[List[ValDef]], rettype: Tree): Tree =
      genCall(q"$target", q"$selectorVal", argsList, rettype)


    private def genCall(target: Tree, selectorVal: Tree, argsList: List[List[ValDef]], rettype: Tree): Tree = {
      val argnames = argsList match {
        case Nil => Nil
        case List(args) => args map {
          case t@ValDef(_, name, tpe, _) =>
            // TODO: do we really need this casting? without, the NIR compiler complains about a missing type tag
            castMode(tpe) match {
              case CastMode.TypeArg =>
                q"$name.asInstanceOf[AnyRef]"
              case _ => q"$name"
            }
        }
        case x =>
          c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
          ???
      }
      // TODO: check if intermediate casting is still required
      castMode(rettype) match {
        case CastMode.Direct =>
          q"scalanative.native.objc.runtime.objc_msgSend($target,$selectorVal,..$argnames).cast[$rettype]"
        case CastMode.Object =>
          q"scalanative.native.objc.runtime.objc_msgSend($target,$selectorVal,..$argnames).cast[Object].cast[$rettype]"
        case CastMode.InstanceOf | CastMode.TypeArg =>
          q"scalanative.native.objc.runtime.objc_msgSend($target,$selectorVal,..$argnames).cast[Object].asInstanceOf[$rettype]"
      }
    }

    // As of scala-native 0.3.2, casting from unsigned (UInt, ULong, ...) to signed (CInt, CLong, ...)
    // is not supported. Hence we need to add an additional cast to Object in these cases.
    private def castMode(rettype: Tree): CastMode.Value = try{
      getQualifiedTypeName(rettype, withMacrosDisabled = true, dealias = true) match {
        case "Boolean" | "Int" | "Long" | "Short" |
             "scala.scalanative.native.UShort" =>
          CastMode.Object
        case "Float" | "Double" =>
          CastMode.InstanceOf
        case x =>
          CastMode.Direct
      }
      // TODO: we shouldn't need this catch - can we avoid this Excpetion?
    } catch {
      case _: Throwable => CastMode.TypeArg
    }

    object CastMode extends Enumeration {
      val Direct = Value
      val Object = Value
      val InstanceOf = Value
      val TypeArg  = Value
    }

  }
}