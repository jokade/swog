package scala.scalanative.objc

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

  private[objc] class ObjCMacro(val c: whitebox.Context) extends BaseMacro {
    override def isObjCClass: Boolean = false
  }
  private[objc] class ObjCClassMacro(val c: whitebox.Context) extends BaseMacro {
    override def isObjCClass: Boolean = true
  }

  // marks that a class wraps an ObjC-object in the __ptr var
  class Wrapper extends StaticAnnotation

  private[objc] abstract class BaseMacro
    extends MacroAnnotationHandler
      with ObjCMacroTools {

    import c.universe._

    def isObjCClass: Boolean

    override val annotationName: String = "scala.scalanative.objc.ObjC"
    override val supportsClasses: Boolean = true
    override val supportsTraits: Boolean = true
    override val supportsObjects: Boolean = true
    override val createCompanion: Boolean = true

    private val wrapperAnnot = q"new scalanative.objc.ObjC.Wrapper"

    override def analyze: Analysis = super.analyze andThen {
      case (cls: TypeParts, data) =>
        val companionStmts =
          if (cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT))
            List(genWrapperImplicit(cls.name, cls.tparams))
          else
            Nil
        // collect selectors and signatures of objc_msgSend() to be emitted into companion object body
        val (selectors,externals) = cls.body.collect {
          case t @ DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
            (genSelector(name, args),genMsgSend(t))
        }.unzip
        (cls,
          data
            .withCompanionName(cls.name.toTermName)
            .withSelectors(selectors)
            .withExternals(externals.toSet)
            .withAdditionalCompanionStmts(companionStmts))
      case default => default
    }

    override def transform: Transformation = super.transform andThen {
      /* transform class */
      case cls: ClassTransformData =>
        val ctorParams = transformCtorParams(cls.modParts.params)

        val parents = transformParents(cls.modParts.parents)

        val annots =
          if (isObjCClass) cls.modParts.modifiers.annotations
          else cls.modParts.modifiers.annotations :+ wrapperAnnot

        val transformedBody = transformBody(cls.modParts.body,cls.modParts.name)(cls.data)

        cls
          .updAnnotations(annots)
          .updCtorParams(ctorParams)
          .updParents(parents)
          .updBody(ccastImport +: transformedBody)
      //.updCtorMods(Modifiers(Flag.PROTECTED))  // ensure that the class can't be instatiated using new

      /* transform traits */
      case trt: TraitTransformData =>

//        val parents = transformParents(trt.modParts.parents)

        val annots =
          if (isObjCClass) trt.modParts.modifiers.annotations
          else trt.modParts.modifiers.annotations :+ wrapperAnnot

        val transformedBody = transformBody(trt.modParts.body,trt.modParts.name)(trt.data)

        trt
          .updAnnotations(annots)
//          .updParents(parents)
          .updBody(ccastImport +: transformedBody)

      /* transform companion object */
      case obj: ObjectTransformData =>
        // val containing the class reference
        val objcCls =
          q"""lazy val __cls = {
              import scalanative._
              import unsafe._
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
              val call = genCall(clsTarget, sel._2, t)(obj.data) //q"objc.objc_msgSend(_cls,$selectorVal,${paramNames(t)})"
              (Some(sel), DefDef(mods, name, types, args, rettype, call))
            case stmt => (None, stmt)
          }.unzip
        // create selector definitions
        val selectorDefs = (clsSelectors ++ objSelectors.collect { case Some(sel) => sel })
          .toMap
          .map(p => genSelectorDef(p._1, p._2))
        // create objc_msgSend() signatures
        val externals = q"@extern object __ext {import scalanative.objc.runtime._; ..${obj.data.externals.map(_.decl)}}"
        // new body = (transformed) statements ++ selector definitions
        val transformedBody = (Seq(ccastImport) ++ selectorDefs :+ objcCls :+ externals) ++ objStmts ++ obj.data.additionalCompanionStmts
        obj.updBody(transformedBody)
      case default => default
    }

    private def transformBody(body: Seq[Tree], typeName: TypeName)(implicit data: Data): Seq[Tree] =
      (if (data.replaceClassBody.isDefined)
        data.replaceClassBody.get
      else body)
        .map {
          case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
            val selectorTerm = q"${typeName.toTermName}.${genSelector(name, args)._2}"
            val call =
              if (isObjCClass)
                genCall(q"__cls", selectorTerm, t)
              else
                genCall(q"this.__ptr", selectorTerm, t)
            DefDef(mods, name, types, args, rettype, call)
          case x => x
        }

    private def genWrapperImplicit(tpe: TypeName, tparams: Seq[Tree]): Tree =
      tparams.size match {
        case 0 =>
          q"""implicit object __wrapper extends scalanative.objc.ObjCWrapper[$tpe] {
            def __wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
          }
          """
        case 1 =>
          q"""implicit object __wrapper extends scalanative.objc.ObjCWrapper[$tpe[_]] {
            def __wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
          }
          """
        case 2 =>
          q"""implicit object __wrapper extends scalanative.objc.ObjCWrapper[$tpe[_,_]] {
            def __wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
          }
          """
      }

    private def transformCtorParams(params: Seq[Tree]): Seq[Tree] =
      if(isObjCClass) Nil
      else Seq(q"override val __ptr: scalanative.unsafe.Ptr[Byte]")

    private def transformParents(parents: Seq[Tree]): Seq[Tree] =
      if(isObjCClass) parents
      else parents map (p => (p,getType(p))) map {
        case (tree,tpe) if tpe =:= tObjCObject || tpe.typeSymbol.isAbstract => tree
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
      findAnnotation(m.mods.annotations,"scala.scalanative.objc.returnsThis").isDefined

    private def useWrapper(m: DefDef): Boolean =
      findAnnotation(m.mods.annotations,"scala.scalanative.objc.useWrapper").isDefined


    private def genCall(target: TermName, selectorVal: TermName, scalaDef: DefDef)(implicit data: Data): Tree =
      genCall(q"$target",q"$selectorVal",scalaDef)

    private def genCall(target: Tree, selectorVal: Tree, scalaDef: DefDef)(implicit data: Data): Tree = {
      val args = (scalaDef.vparamss match {
        case Nil => Nil
        case List(argdefs) => argdefs
        case List(inargs,outargs) => inargs ++ outargs
        case x =>
          c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
          ???
      }) map {
          case t@ValDef(_, name, tpt, _) =>
            if (isObjCObject(tpt))
              q"if($name == null) null else $name.__ptr"
            else q"$name"
        }

      val resultType = getObjCType(scalaDef.tpt)

      val argNames = (1 to args.size).map(p => TermName("arg"+p))
      val argDefs = argNames.zip(args).map(p => q"val ${p._1} = ${p._2}")

      val msgSendName = genMsgSendName(scalaDef)
      val call = q"""${data.companionName}.__ext.$msgSendName($target,$selectorVal,..$argNames)"""

      if( returnsThis(scalaDef) )
        q"..$argDefs;$call;this"
      else if ( useWrapper(scalaDef) )
        q"..$argDefs;__wrapper.__wrap($call)"
      else
        q"..$argDefs;${wrapResult(call,resultType)}"
    }

  }
}