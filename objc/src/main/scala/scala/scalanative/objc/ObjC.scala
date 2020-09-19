package scala.scalanative.objc

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.{TypecheckException, whitebox}
import scala.scalanative.cobj.internal.CommonHandler

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

    override protected def tpeDefaultParent: c.universe.Tree = tpeObjCObject
  }
  private[objc] class ObjCClassMacro(val c: whitebox.Context) extends BaseMacro {
    override def isObjCClass: Boolean = true
    override protected def tpeDefaultParent: c.universe.Tree = tpeObjCObject
  }


  // marks that a class wraps an ObjC-object in the __ptr var
  class Wrapper extends StaticAnnotation

  private[objc] abstract class BaseMacro
    extends CommonHandler {

    import c.universe._

    def isObjCClass: Boolean

    protected def cstring(s: String) = q"scalanative.unsafe.CQuote(StringContext($s)).c()"
    protected val tObjCObject = c.weakTypeOf[ObjCObject]
    protected val tpeObjCObject = tq"$tObjCObject"
    protected val msgSendNameAnnot = Modifiers(NoFlags,typeNames.EMPTY,List(q"new name(${Literal(Constant("objc_msgSend"))})"))
    protected val msgSendFpretNameAnnot = Modifiers(NoFlags,typeNames.EMPTY,List(q"new name(${Literal(Constant("objc_msgSend_fpret"))})"))
    protected val clsTarget = TermName("__cls")
    protected val tpeId = tq"scalanative.objc.runtime.id"
    protected val tpeSEL = tq"scalanative.objc.runtime.SEL"

    // see https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html
    sealed abstract class TypeCode(val code: String, // the Objective-C type code
                                   val suffix: String, // suffix used to generate distinctive names for msgSend signatures
                                   val typeTree: Tree
                                  ) {
      override def toString(): String = suffix
    }
    object TypeCode {
      object byte extends TypeCode("c","y",tpeByte)
      object ubyte extends TypeCode("C","Y",tpeUByte)
      object char extends TypeCode("c","c",tpeChar)
      object uchar extends TypeCode("C","C",tpeUChar)
      object short extends TypeCode("s","s",tpeShort)
      object ushort extends TypeCode("S","S",tpeUShort)
      object int extends TypeCode("i","i",tpeInt)
      object uint extends TypeCode("I","I",tpeUInt)
      object long extends TypeCode("l","l",tpeLong)
      object ulong extends TypeCode("L","L",tpeULong)
      object longlong extends TypeCode("q","q",tpeLongLong)
      object ulonglong extends TypeCode("Q","Q",tpeULongLong)
      object float extends TypeCode("f","f",tpeFloat)
      object double extends TypeCode("d","d",tpeDouble)
      object bool extends TypeCode("B","B",tpeBoolean)
      object void extends TypeCode("v","v",tpeUnit)
      object string extends TypeCode("*","a",tpeCString)
      object obj extends TypeCode("@","p",tpePtrByte)
      val ptr = obj
      object unknown extends TypeCode("@","p",tpePtrByte) // type check failed -> unknonw; but we assume that it is represented as an object pointer

    }


    implicit class ObjCMacroData(var data: Map[String, Any]) {
      type Data = Map[String, Any]
      type Selectors = Seq[(String, TermName)]
      type Statements = Seq[Tree]

      def companionName: TermName = data.getOrElse("companionName",null).asInstanceOf[TermName]
      def withCompanionName(name: TermName): Data = data.updated("companionName",name)

      // selectors to be defined in the companion object
      def selectors: Selectors = data.getOrElse("selectors", Nil).asInstanceOf[Selectors]
      def addSelectors(selectors: Selectors): Data = data.updated("selectors",data.selectors ++ selectors)

      // statements to be executed during ObjC class intialization for @ScalaDefined classes
      // (i.e. the code required to define the ObjC class when the first call to a class method is issued)
      def objcClassInits: Statements = data.getOrElse("objcClassInits", Nil).asInstanceOf[Statements]
      def withObjcClassInits(inits: Statements): Data = data.updated("objcClassInits",inits)

      def replaceClassBody: Option[Statements] = data.getOrElse("replaceClsBody", None).asInstanceOf[Option[Statements]]
      def replaceClassBody_=(stmts: Option[Statements]): Data = {
        data += "replaceClsBody" -> stmts
        data
      }
    }

    override val annotationName: String = "scala.scalanative.objc.ObjC"
    override val supportsClasses: Boolean = true
    override val supportsTraits: Boolean = true
    override val supportsObjects: Boolean = true
    override val createCompanion: Boolean = true

    private val wrapperAnnot = q"new scalanative.objc.ObjC.Wrapper"

    override def analyze: Analysis = super.analyze andThen {
      case (cls: TypeParts, data) =>
        val updData = analyzeTypes(cls)(data)

        val companionStmts =
          if (cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT))
            List(genWrapperImplicit(cls.name, cls.tparams, Seq.empty))
          else
            Nil
        // collect selectors and signatures of objc_msgSend() to be emitted into companion object body
        val (selectors,externals) = cls.body.collect {
          case t @ DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
            (genSelector(name, args),genExternalCallWithTypeCode("objc_msgSend","msgSend_",t))
        }.unzip

        (cls,
          updData
            .withCompanionName(cls.name.toTermName)
            .addSelectors(selectors)
            .addExternals(externals.toMap)
            .addCompanionStmts(companionStmts))
      case default => default
    }

    override def transform: Transformation = super.transform andThen {
      /* transform class */
      case cls: ClassTransformData =>
//        val ctorParams = transformCtorParams(cls.modParts.params)
        val ctorParams =
          if(isObjCClass) (Nil, Nil)
          else
            genTransformedCtorParams(cls)

        val parents = transformParents(cls.modParts.parents,cls.data)

        val annots =
          if (isObjCClass) cls.modParts.modifiers.annotations
          else cls.modParts.modifiers.annotations :+ wrapperAnnot

        val transformedBody = transformBody(cls.modParts.body,cls.modParts.name)(cls.data)

        cls
          .updAnnotations(annots)
          .updCtorParams(ctorParams)
          .updParents(parents)
          .updBody(transformedBody)
      //.updCtorMods(Modifiers(Flag.PROTECTED))  // ensure that the class can't be instatiated using new

      /* transform traits */
      case trt: TraitTransformData =>
        val annots =
          if (isObjCClass) trt.modParts.modifiers.annotations
          else trt.modParts.modifiers.annotations :+ wrapperAnnot

        val transformedBody = transformBody(trt.modParts.body,trt.modParts.name)(trt.data)

        trt
          .updAnnotations(annots)
          .updBody(transformedBody)

      /* transform companion object */
      case obj: ObjectTransformData =>
        // val containing the class reference
        val clsName = obj.modParts.name.toString
        val objcCls = genClsDefn(obj, clsName)
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
          .toSeq
        // create objc_msgSend() signatures
        val externals = genBindingsObject(obj.data)
        val transformedBody = (selectorDefs :+ objcCls :+ externals) ++ objStmts ++ obj.data.additionalCompanionStmts
        obj.updBody(transformedBody)
      case default => default
    }

    protected[objc] def genClsDefn(obj: ObjectTransformData, clsName: String): Tree = {
      q"""lazy val __cls = {
            import scalanative._
            import unsafe._
            ..${obj.data.objcClassInits}
            objc.runtime.objc_getClass(CQuote(StringContext($clsName)).c() )
          }"""
    }

    protected[objc] def transformBody(body: Seq[Tree], typeName: TypeName)(implicit data: Data): Seq[Tree] = {
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
    }


    protected[objc] def isObjCObject(tpt: Tree): Boolean =
      try{
        val tpe = getType(tpt,true)
        isObjCObject(tpe)
      }
      catch {
        case _: Throwable => true
      }

    protected[objc] def isObjCObject(tpe: Type): Boolean = tpe.baseClasses.map(_.asType.toType).exists( t => t <:< tObjCObject )

    // TODO: can we use CommonHandler.transformParents instead?
    protected[objc] def transformParents(parents: Seq[Tree], data: Data): Seq[Tree] =
      if(isObjCClass) parents
      else parents map (p => (p,getType(p))) map {
        case (tree,tpe) if tpe =:= tObjCObject || tpe.typeSymbol.isAbstract => tree
        case (tree,tpe) if tpe <:< tObjCObject =>
          if (data.requiresPtrImpl) q"$tree(__ptr)" else q"$tree(ptr)"
        case (tree,_) => tree
      }


    protected[objc] def genCall(target: TermName, selectorVal: TermName, scalaDef: DefDef)(implicit data: Data): Tree =
      genCall(q"$target",q"$selectorVal",scalaDef)

    protected[objc] def genCall(target: Tree, selectorVal: Tree, scalaDef: DefDef)(implicit data: Data): Tree = {
      val (argList,wrappers) = (scalaDef.vparamss match {
        case Nil =>
          (Nil,Nil)
        case List(argdefs) =>
          (argdefs, Nil)
        case List(inargs,outargs) =>
          val (wrappers,filteredOutargs) = outargs.partition(p => isCObjectWrapper(p.tpt))
          (inargs ++ filteredOutargs, wrappers)
        case x =>
          c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
          ???
      })
      val args = argList.map {
        case t@ValDef(_, name, tpt, _) =>
          if (isObjCObject(tpt))
            q"if($name == null) null else $name.__ptr"
          else q"$name"
      }

      val argNames = (1 to args.size).map(p => TermName("arg"+p))
      val argDefs = argNames.zip(args).map(p => q"val ${p._1} = ${p._2}")

      val msgSendName = genNameWithTypeCodes("msgSend_",scalaDef)

      val call = q"""${data.companionName}.__ext.$msgSendName($target,$selectorVal,..$argNames)"""

      val rhs = wrapExternalCallResult(call,scalaDef.tpt,data,nullable(scalaDef),returnsThis(scalaDef),wrappers)
      q"..$argDefs;$rhs"
    }


    protected[objc] def genSelectorDef(selector: String, selectorTerm: TermName) =
      q"protected lazy val $selectorTerm = scalanative.objc.runtime.sel_registerName(scalanative.unsafe.CQuote(StringContext($selector)).c())"

    protected[objc] def genSelector(name: TermName, args: List[List[ValDef]]): (String, TermName) = {
      val s = genSelectorString(name, args)
      (s, genSelectorTerm(s))
    }


    protected[objc] def genSelectorTermString(selectorString: String): String =
      "__sel_"+selectorString.replaceAll(":","_")

    protected[objc] def genSelectorTerm(selectorString: String): TermName = {
      TermName(genSelectorTermString(selectorString))
    }


    protected[objc] def genSelectorString(name: TermName, args: List[List[ValDef]]): String =
      name.toString.replaceAll("_",":")


    protected[objc] def genTypeCode(tpt: Tree): TypeCode =
      try{
        genTypeCode( getType(tpt, true) )
      } catch {
        // if type check fails => return 'unknown'
        case ex: TypecheckException => TypeCode.unknown
      }

    protected[objc] def genTypeCode(tpe: Type): TypeCode =
      tpe.dealias match {
        case t if t <:< tByte => TypeCode.byte
        case t if t <:< tUByte => TypeCode.ubyte
        case t if t <:< tShort => TypeCode.short
        case t if t <:< tUShort => TypeCode.ushort
        case t if t <:< tInt => TypeCode.int
        case t if t <:< tUInt => TypeCode.uint
        case t if t <:< tLong => TypeCode.long
        case t if t <:< tULong => TypeCode.ulong
        case t if t <:< tBoolean => TypeCode.bool
        case t if t <:< tChar => TypeCode.char
        case t if t <:< tDouble => TypeCode.double
        case t if t <:< tFloat => TypeCode.float
        case t if t <:< tObjCObject => TypeCode.obj
        case t if t <:< tPtr => TypeCode.ptr
        case t if t <:< tUnit => TypeCode.void
        case _ =>
          c.error(c.enclosingPosition, s"unsupported type: $tpe")
          ???
      }

    private def mapTypeForExternalCall(tpt: Tree): Tree = getType(tpt,true) match {
      case t if t <:< tAnyVal || t <:< tUByte || t <:< tUShort || t <:< tUInt || t <:< tULong => tpt
      case t if t <:< tObjCObject || t <:< tPtr => tpePtrByte
      case _ =>
        c.error(c.enclosingPosition,s"unsupported type: $tpt")
        ???
    }

    /**
     * Generates a method name using the provided prefix and the signature of the passed DefDef.
     *
     * @example
     *  For the method signature `foo(i: Int, f: Boolean): Ptr[Byte]`
     *  and the prefix `msgSend_` the result will be 'msgSend_piB'
     */
    protected[objc] def genNameWithTypeCodes(prefix: String, scalaDef: DefDef): TermName = {
      val suffix = scalaDef.vparamss match {
        case ArgsAndWrappers(None,_) => ""
        case ArgsAndWrappers(Some(argdefs), _) => argdefs.map(p => genTypeCode(p.tpt)).mkString
        case x =>
          c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
          ???
      }
      val retType = genTypeCode(scalaDef.tpt)
      TermName(prefix+retType+suffix)
    }

    protected[objc] def genExternalCallWithTypeCode(externalName: String, methodPrefix: String, scalaDef: DefDef): External = {
      val name = genNameWithTypeCodes(methodPrefix, scalaDef)

      val nameAnnot = Modifiers(NoFlags,typeNames.EMPTY,List(q"new name(${Literal(Constant(externalName))})"))

      val (args, wrappers) = scalaDef.vparamss match {
        case ArgsAndWrappers(None,wrappers) =>
          (Nil, wrappers)
        case ArgsAndWrappers(Some(argdefs),wrappers) =>
          val args = argdefs.map( p => mapTypeForExternalCall(p.tpt)).zipWithIndex.map { t =>
            val name = TermName("arg"+t._2)
            q"val $name: ${t._1}"
          }
          (args, wrappers)
        case _ =>
          c.error(c.enclosingPosition,"extern methods with more than two argument lists are not supported")
          ???
      }

      val rettype = genTypeCode(scalaDef.tpt).typeTree

      name.toString -> (externalName -> q"$nameAnnot def $name(self: $tpeId, sel: $tpeSEL, ..$args): $rettype = $expExtern" )
    }


  }
}