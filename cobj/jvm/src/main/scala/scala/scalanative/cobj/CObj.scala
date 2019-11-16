package scala.scalanative.cobj

import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.scalanative.cobj.internal.{CObjBase, CommonHandler}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class CObj(prefix: String = null, namingConvention: NamingConvention.Value = NamingConvention.SnakeCase) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CObj.Macro.impl
}

object CObj {

  class CObjWrapper extends StaticAnnotation
  
  private[cobj] class Macro(val c: whitebox.Context) extends CObjBase {
    import c.universe._

    override def transform: Transformation = super.transform andThen {
      /* transform class */
      case cls: ClassTransformData =>
        val transformedBody = genTransformedTypeBody(cls)
        cls
          .updBody(transformedBody)
//          .addAnnotations(cobjWrapperAnnotation)
          .updCtorParams(genTransformedCtorParams(cls))
          .updParents(genTransformedParents(cls))
      /* transform trait */
      case trt: TraitTransformData =>
        val transformedBody = genTransformedTypeBody(trt)
        trt
          .updBody(transformedBody)
//          .addAnnotations(cobjWrapperAnnotation)
          .updParents(genTransformedParents(trt))
      /* transform companion object */
      case obj: ObjectTransformData =>
//        val transformedBody = genTransformedCompanionBody(obj) ++ obj.data.additionalCompanionStmts :+ genBindingsObject(obj.data)
        val transformedBody = genTransformedCompanionBody(obj)
        obj.updBody(transformedBody)
      case default => default
    }


//    override protected def genTransformedTypeBody(t: TypeTransformData[TypeParts]): Seq[c.universe.Tree] = {
//   
//      super.genTransformedTypeBody(t)
//    }


    override protected def genTransformedCompanionBody(t: TransformData[CommonParts]): Seq[c.universe.Tree] = {
      val updBody =
        t.modParts.body.map{
          case scalaDef:DefDef if isExtern(scalaDef.rhs) => 
            genJNACall(scalaDef,true)(t.data)
          case x => x
        }

      val jnaDefs = genJNABindings(t.data.externals)
      val jnaName = Literal(Constant(t.origParts.fullName))
      
      updBody ++ Seq(
        q"""trait __IFace extends com.sun.jna.Library {
              ..$jnaDefs
            }
         """,
        q"""
            lazy val __ext = scalanative.interop.platform.loadJNALibrary($jnaName, classOf[__IFace])
         """
      )
    }

    private def genJNABindings(externals: MacroData#Externals) =
      externals.toSeq.map {
        case (_,(_, DefDef(mods,name,tps,params,rtpe,_))) =>
          DefDef(Modifiers(mods.flags | Flag.DEFERRED),name,tps,params,rtpe,EmptyTree)
      }
    
    private def genJNACall(scalaDef: DefDef, isClassMethod: Boolean)(implicit data: Data) = {
      val externalName = data.externals(scalaDef.name.toString)._1
      genExternalCall(externalName,scalaDef,isClassMethod,data)
    }

    protected override def genExternalCall(externalName: String, scalaDef: DefDef, isClassMethod: Boolean, data: Data): DefDef = {
      import scalaDef._
      val (args,wrappers) = vparamss match {
        case Nil => (None,Nil)
        case List(args) => (Some(transformExternalCallArgs(args,data)),Nil)
        case List(inargs,outargs) =>
          val (wrappers,filteredOutargs) = outargs.partition(p => isCObjectWrapper(p.tpt))
          (Some( transformExternalCallArgs(inargs,data,false,wrappers) ++ transformExternalCallArgs(filteredOutargs,data,false,wrappers) ), wrappers)
        case _ =>
          c.error(c.enclosingPosition,"extern methods with more than two parameter lists are not supported for @CObj classes")
          ???
      }
      val external = TermName(externalName)
      val call = args match {
        case Some(as) if isClassMethod => q"__ext.$external(..$as)"
        case Some(as) => q"__ext.$external(__ptr,..$as)"
        case None if isClassMethod => q"__ext.$external()"
        case None => q"__ext.$external(__ptr)"
      }
      val rhs = wrapExternalCallResult(call,tpt,data,nullable(scalaDef),returnsThis(scalaDef),wrappers)

      DefDef(mods,name,tparams,vparamss,tpt,rhs)
    }

    override protected def genExternalName(prefix: String, scalaDef: c.universe.DefDef)(implicit data: Data): String =
      nameAnnotation(scalaDef)
        .getOrElse(super.genExternalName(prefix,scalaDef))
  }
}
