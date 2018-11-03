package scala.scalanative.native.objc

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

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

    private val wrapperAnnot = q"new scalanative.native.objc.ObjC.Wrapper"

    override def analyze: Analysis = super.analyze andThen {
      case (cls: TypeParts, data) =>
        val companionStmts =
          if( cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT) )
            List(genWrapperImplicit(cls.name,cls.tparams))
          else
            Nil
        // collect selectors to be emitted into companion object body
        val selectors = cls.body.collect {
          case DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
            genSelector(name, args)
        }
        (cls,
          data
            .withSelectors(selectors)
            .withAdditionalCompanionStmts(companionStmts) )
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
                    genCall(q"__cls", selectorTerm,t)
                  else
                    genCall(q"this.__ptr",selectorTerm,t)
                DefDef(mods, name, types, args, rettype, call)
              case x => x
            }

        cls
          .updAnnotations(annots)
          .updCtorParams(ctorParams)
          .updParents(parents)
          .updBody(ccastImport +: transformedBody)
      //.updCtorMods(Modifiers(Flag.PROTECTED))  // ensure that the class can't be instatiated using new

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
              val call = genCall(clsTarget, sel._2, t) //q"objc.objc_msgSend(_cls,$selectorVal,${paramNames(t)})"
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

    private def genWrapperImplicit(tpe: TypeName, tparams: Seq[Tree]): Tree =
      if(tparams.isEmpty)
        q"""implicit object __wrapper extends scalanative.native.objc.ObjCWrapper[$tpe] {
            def __wrap(ptr: scalanative.native.Ptr[Byte]) = new $tpe(ptr)
          }
       """
      else
        q"""implicit object __wrapper extends scalanative.native.objc.ObjCWrapper[$tpe[_]] {
            def __wrap(ptr: scalanative.native.Ptr[Byte]) = new $tpe(ptr)
          }
       """

    private def transformCtorParams(params: Seq[Tree]): Seq[Tree] =
      if(isObjCClass) Nil
      else Seq(q"override val __ptr: Ptr[Byte]")

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

    private def returnsThis(m: DefDef): Boolean =
      findAnnotation(m.mods.annotations,"scala.scalanative.native.objc.returnsThis").isDefined

    private def useWrapper(m: DefDef): Boolean =
      findAnnotation(m.mods.annotations,"scala.scalanative.native.objc.useWrapper").isDefined

    //  private def wrapResult(result: Tree, resultType: Tree): Tree =
    //    if( isObjCObject(resultType) )
    //      q"new $resultType($result)"
    //    else
    //      q"$result.cast[$resultType]"

    private def genCall(target: TermName, selectorVal: TermName, scalaDef: DefDef): Tree =
      genCall(q"$target",q"$selectorVal",scalaDef)

    private def genCall(target: Tree, selectorVal: Tree, scalaDef: DefDef): Tree = {
      val args = scalaDef.vparamss match {
        case Nil => Nil
        case List(argdefs) => argdefs map {
          case t@ValDef(_, name, tpt, _) =>
            if (isObjCObject(tpt))
              q"$name.__ptr"
            else q"$name"
        }
        case x =>
          c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
          ???
      }

      val resultType = getObjCType(scalaDef.tpt)

      val call = resultType match {
        case Some(t) if t <:< tFloat =>
          q"scalanative.native.objc.runtime.objc_msgSend_Float($target,$selectorVal,..$args)"
        case Some(t) if t <:< tDouble =>
          q"scalanative.native.objc.runtime.objc_msgSend_Double($target,$selectorVal,..$args)"
        case _ =>
          q"scalanative.native.objc.runtime.objc_msgSend($target,$selectorVal,..$args)"
      }
      if( returnsThis(scalaDef) )
        q"$call;this"
      else if ( useWrapper(scalaDef) )
        q"__wrapper.__wrap($call)"
      else
        wrapResult(call,resultType)
    }

  }
}