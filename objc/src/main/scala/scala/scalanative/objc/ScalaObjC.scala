package scala.scalanative.objc

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class ScalaObjC() extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ScalaObjC.Macro.impl
}

object ScalaObjC {

  private[objc] class Macro(val c: whitebox.Context)
    extends ObjC.BaseMacro {
    import c.universe._

    override val annotationName: String = "SNDefined"
    override val supportsClasses: Boolean = true
    override val supportsTraits: Boolean = false
    override val supportsObjects: Boolean = true
    override val createCompanion: Boolean = true
    override val isObjCClass: Boolean = false
    override protected def tpeDefaultParent: c.universe.Tree = tpeObjCObject

    val tpeRetain = typeOf[retain]

    implicit class ScalaObjCMacroData(var data: Map[String, Any]) {
      type Data = Map[String, Any]

      def selfRef: ValDef = data.getOrElse("selfRef", null).asInstanceOf[ValDef]
      def withSelfRef(selfRef: ValDef): Data = data.updated("selfRef", selfRef)
    }

    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data: Data) =>

        val exposedMembers = getExposedMembers(cls.body)

        val classInits =
          objcClassDef(cls,exposedMembers) //++

        // collect all required ObjC selectors (every selector is stored as a lazy val on the companion object)
        val selectors = exposedMembers.map( _.selector ) ++
          // add setter selectors for public vars
          exposedMembers.filter(_.provideSetter).map{m =>
            val name = genSetterSelectorName(m.name)
            (name,genSelectorTerm(name)) //TermName("__sel_"+name))
          }

        val updData =
          data
            .withObjcClassInits(classInits)
            .addCompanionStmts(allocDef(cls))
            .addSelectors(selectors)
        (cls,updData)
      case x => x
    }


    // generate code to define the ObjC proxy class
    private def objcClassDef(cls: ClassParts, exposedMembers: Seq[ExposedMember]) = Seq[Tree] {
      val clsName = cstring(cls.name.toString)
      val parent = c.typecheck(cls.parents.head,c.TYPEmode).tpe match {
        case p if p =:= weakTypeOf[Object] => q"scalanative.objc.defaultRootObject"
        case p if p <:< weakTypeOf[ObjCObject] => q"${p.typeSymbol.companion}.__cls"
        case _ =>
          error("@ScalaObjC class must be a descendant of ObjCObject")
          ???
      }

      val exposedSetters = exposedMembers.filter(_.provideSetter).map(registerVarSetter(cls.name))

      q"""import scalanative._
          import objc.runtime._
          import objc.helper._
          val newCls = objc_allocateClassPair($parent,$clsName,0)
          objc.helper.addScalaInstanceIVar(newCls)
          val metaCls = object_getClass(newCls)
          class_addMethod(
            metaCls,
            sel_allocWithZone,
            __allocWithZone,
            c"@:@")
          ..${exposedMembers map registerExposedMember(cls.name)}
          ..${exposedSetters}
          objc_registerClassPair(newCls)
       """
    }

    private def allocDef(cls: ClassParts) = Seq[Tree] {
      val clsName = cls.name
      q"""val __allocWithZone: scalanative.objc.runtime.IMP = {
          import scalanative.unsafe._
          import scalanative.objc.runtime._
            new CFuncPtr3[id,SEL,id,id]{ def apply(cls: id, sel: SEL, zone: id) = {
              import scalanative.objc
              val ref = objc.helper.allocWithZone(zone,__cls)
              val instance = new $clsName(ref)
              objc.helper.setScalaInstanceIVar(ref,instance)
              ref
            }
          }}
       """
    }

    private def genCallback(clsName: TypeName, argTypes: Seq[Type], args: Seq[Tree], resultType: Type, call: Tree) = {
      val ivar = q"val o = scalanative.objc.helper.getScalaInstanceIVar[${clsName}](__id)"
      val apply = q"def apply(__id: id, __sel: SEL, ..$args) = { $ivar; $call }"
      argTypes.length match {
        case 0 => q"new scalanative.unsafe.CFuncPtr2[id,SEL,..$argTypes,$resultType] { $apply }"
        case 1 => q"new scalanative.unsafe.CFuncPtr3[id,SEL,..$argTypes,$resultType] { $apply }"
        case 2 => q"new scalanative.unsafe.CFuncPtr4[id,SEL,..$argTypes,$resultType] { $apply }"
        case 3 => q"new scalanative.unsafe.CFuncPtr5[id,SEL,..$argTypes,$resultType] { $apply }"
        case 4 => q"new scalanative.unsafe.CFuncPtr6[id,SEL,..$argTypes,$resultType] { $apply }"
        case 5 => q"new scalanative.unsafe.CFuncPtr7[id,SEL,..$argTypes,$resultType] { $apply }"
        case 6 => q"new scalanative.unsafe.CFuncPtr8[id,SEL,..$argTypes,$resultType] { $apply }"
        case 7 => q"new scalanative.unsafe.CFuncPtr9[id,SEL,..$argTypes,$resultType] { $apply }"
        case 8 => q"new scalanative.unsafe.CFuncPtr10[id,SEL,..$argTypes,$resultType] { $apply }"
        case 9 => q"new scalanative.unsafe.CFuncPtr11[id,SEL,..$argTypes,$resultType] { $apply }"
        case 10 => q"new scalanative.unsafe.CFuncPtr12[id,SEL,..$argTypes,$resultType] { $apply }"
        case 11 => q"new scalanative.unsafe.CFuncPtr13[id,SEL,..$argTypes,$resultType] { $apply }"
        case 12 => q"new scalanative.unsafe.CFuncPtr14[id,SEL,..$argTypes,$resultType] { $apply }"
        case 13 => q"new scalanative.unsafe.CFuncPtr15[id,SEL,..$argTypes,$resultType] { $apply }"
        case 14 => q"new scalanative.unsafe.CFuncPtr16[id,SEL,..$argTypes,$resultType] { $apply }"
        case 15 => q"new scalanative.unsafe.CFuncPtr17[id,SEL,..$argTypes,$resultType] { $apply }"
        case 16 => q"new scalanative.unsafe.CFuncPtr18[id,SEL,..$argTypes,$resultType] { $apply }"
        case 17 => q"new scalanative.unsafe.CFuncPtr19[id,SEL,..$argTypes,$resultType] { $apply }"
        case 18 => q"new scalanative.unsafe.CFuncPtr20[id,SEL,..$argTypes,$resultType] { $apply }"
        case 19 => q"new scalanative.unsafe.CFuncPtr21[id,SEL,..$argTypes,$resultType] { $apply }"
        case 20 => q"new scalanative.unsafe.CFuncPtr22[id,SEL,..$argTypes,$resultType] { $apply }"
        case x =>
          c.error(c.enclosingPosition, s"function pointers with $x arguments are not supported")
          ???
      }
    }

    private def registerExposedMember(clsName: TypeName)(m: ExposedMember): Tree = {
      val typeEncoding = cstring( genTypeEncoding(m) )
      val ivar = q"val o = scalanative.objc.helper.getScalaInstanceIVar[${clsName}](__id)"
      val argTypes =  m.params.map( _.tpt )
      val argNames = m.params.map( _.name )
      val call = if(m.hasParamList) q"o.${m.name}(..$argNames)" else q"o.${m.name}"
      val apply = q"def apply(__id: id, __sel: SEL, ..${m.params}) = { $ivar; $call }"
      val funcPtr = m.params.length match {
        case 0 => q"new scalanative.unsafe.CFuncPtr2[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 1 => q"new scalanative.unsafe.CFuncPtr3[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 2 => q"new scalanative.unsafe.CFuncPtr4[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 3 => q"new scalanative.unsafe.CFuncPtr5[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 4 => q"new scalanative.unsafe.CFuncPtr6[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 5 => q"new scalanative.unsafe.CFuncPtr7[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 6 => q"new scalanative.unsafe.CFuncPtr8[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 7 => q"new scalanative.unsafe.CFuncPtr9[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 8 => q"new scalanative.unsafe.CFuncPtr10[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 9 => q"new scalanative.unsafe.CFuncPtr11[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 10 => q"new scalanative.unsafe.CFuncPtr12[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 11 => q"new scalanative.unsafe.CFuncPtr13[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 12 => q"new scalanative.unsafe.CFuncPtr14[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 13 => q"new scalanative.unsafe.CFuncPtr15[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 14 => q"new scalanative.unsafe.CFuncPtr16[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 15 => q"new scalanative.unsafe.CFuncPtr17[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 16 => q"new scalanative.unsafe.CFuncPtr18[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 17 => q"new scalanative.unsafe.CFuncPtr19[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 18 => q"new scalanative.unsafe.CFuncPtr20[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 19 => q"new scalanative.unsafe.CFuncPtr21[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case 20 => q"new scalanative.unsafe.CFuncPtr22[id,SEL,..$argTypes,${m.tpe.get}] { $apply }"
        case x =>
          c.error(c.enclosingPosition, s"function pointers with $x arguments are not supported")
          ???
      }
      q"""class_addMethod(newCls,${m.selector._2},$funcPtr,$typeEncoding)"""
    }

    private def registerVarSetter(clsName: TypeName)(m: ExposedMember) = {
      val selector = genSelectorTerm( genSetterSelectorName(m.name) )
      val valueType = m.tpe.get
      val argType = valueType match {
        case t if t <:< tCObject => tPtrByte
        case x => x
      }
      val typeEncoding = cstring( "@:" + genTypeCode(argType) )
      val value = q"val value = new $valueType(__v)"
      val call = q"$value; o.${m.name} = value"
      val cb = genCallback(clsName,List(argType),List(q"val __v: $argType"),tUnit,call)
      q"""class_addMethod(newCls,$selector,$cb,$typeEncoding)"""
    }


    private def getExposedMembers(body: Seq[Tree]): Seq[ExposedMember] =
      body
        .collect {
          case m: DefDef if isPublic(m.mods) =>
            val returnType = getType(m.tpt,withMacrosDisabled = true)
            val args = m.vparamss match {
              case Nil => Nil
              case List(xs) => xs
              case _ =>
                c.error(c.enclosingPosition,"public methods with more than one argument list are not supported for @ScalaObjC classes")
                ???
            }
            ExposedMember(m,args,args.nonEmpty, tpe = Some(returnType), customSelector = findCustomSelector(m.mods.annotations))
          case p: ValDef if isPublic(p.mods) =>
            val tpe = getType(p.tpt,withMacrosDisabled = true)
            val exp = ExposedMember(p,Nil,hasParamList = false, provideSetter = p.mods.hasFlag(Flag.MUTABLE), tpe = Some(tpe), retain = hasAnnotation(p.mods.annotations,tpeRetain) )
            exp
        }


//    private def methodProxyName(m: ExposedMember): TermName =
//      if(m.provideSetter) methodProxyName(m.name.toString)
//      else {
//        val name = m.params.map(_.name).mkString(m.name.toString+"_","_","")
//        methodProxyName(name)
//      }
//    private def methodProxyName(name: String): TermName = TermName("__m_"+name)

//    private def genExposedMethodProxy(cls: ClassParts)(m: ExposedMember) = {
//      import m._
//      val call = if(hasParamList) q"o.$name(..${paramNames(params)})" else q"o.$name"
//      val result = m.tpe match {
//        case Some(t) if t <:< tObjCObject => q"$call.__ptr"
//        case _ => call
//      }
//      q"""def ${methodProxyName(m)}(self: scalanative.objc.runtime.id, sel: scalanative.objc.runtime.SEL, ..$params) = {
//            val o = scalanative.objc.helper.getScalaInstanceIVar[${cls.name}](self)
//            $result
//          }
//       """
//    }

    private def genExposedVarSetterProxy(cls: ClassParts, data: Data)(m: ExposedMember) = {
      q""
//      import m._
//      def setValue =
//        if(m.retain) {
//          val result = wrapResult(q"value.retain()",tpe)
////          val result = wrapExternalCallResult(q"value.retain()",tpe,data,isNullable = false, returnsThis = false)
//          q"""if(o.$name != null)
//                o.$name.release()
//              o.$name = $result
//           """
//        }
//        else {
//          val result = wrapResult(q"value",tpe)
//          q"""o.$name = $result"""
//        }
//
//      q"""def ${methodProxyName(genSetterSelectorName(m.name))}(self: scalanative.native.objc.runtime.id, sel: scalanative.native.objc.runtime.SEL, value: scalanative.native.objc.runtime.id) = {
//            val o = scalanative.native.objc.helper.getScalaInstanceIVar[${cls.name}](self)
//            $setValue
//          }
//       """
    }

    private def genTypeEncoding(m: ExposedMember): String = {
      val returnType =
        if(m.isSetter) "v"
        else m.tpe.map(genTypeCode).getOrElse("v") // genTypeCode(m.tpe.getOrElse(tUnit))

      returnType + "@:" + m.params.map(x => genTypeCode(x.tpt)).mkString
    }

    private def isPublic(mods: Modifiers): Boolean =
      !( mods.hasFlag(Flag.PROTECTED) || mods.hasFlag(Flag.PRIVATE) )

/*
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

    private def genWrapperImplicit(tpe: TypeName, tparams: Seq[Tree]): Tree =
      tparams.size match {
        case 0 =>
          q"""implicit object __wrapper extends scalanative.native.objc.ObjCWrapper[$tpe] {
            def __wrap(ptr: scalanative.native.Ptr[Byte]) = scalanative.native.objc.helper.getScalaInstanceIVar[$tpe](ptr)
          }
          """
        case 1 =>
          q"""implicit object __wrapper extends scalanative.native.objc.ObjCWrapper[$tpe[_]] {
            def __wrap(ptr: scalanative.native.Ptr[Byte]) = scalanative.native.objc.helper.getScalaInstanceIVar[$tpe[_]](ptr)
          }
          """
        case 2 =>
          q"""implicit object __wrapper extends scalanative.native.objc.ObjCWrapper[$tpe[_,_]] {
            def __wrap(ptr: scalanative.native.Ptr[Byte]) = scalanative.native.objc.helper.getScalaInstanceIVar[$tpe[_,_]](ptr)
          }
          """
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
*/
    private def genSetterSelectorName(name: TermName): String =
      "set" + name.toString.head.toUpper + name.toString.tail + ":"
/*
    object CastMode extends Enumeration {
      val Direct = Value
      val Object = Value
      val InstanceOf = Value
      val TypeArg  = Value
    }
*/
    def findCustomSelector(annots: Seq[Tree]): Option[String] = {
      findAnnotation(annots,"selector")
        .map(t => extractAnnotationParameters(t,Seq("name")) )
        .map(_.apply("name"))
        .map(t => extractStringConstant(t.get).get)
    }

    case class ExposedMember(member: ValOrDefDef,
                             params: List[ValDef],
                             hasParamList: Boolean,
                             tpe: Option[Type] = None,
                             provideSetter: Boolean = false,
                             customSelector: Option[String] = None,
                             retain: Boolean = true,
                             isSetter: Boolean = false) {
      lazy val selector: (String,TermName) = customSelector match {
        case Some(sel) => (sel,genSelectorTerm(sel))
        case _ => genSelector(name,List(params))
      }
      def name: TermName = member.name
    }

  }

}
