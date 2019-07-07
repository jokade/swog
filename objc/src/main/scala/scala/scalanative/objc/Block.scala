package scala.scalanative.objc

import de.surfice.smacrotools.BlackboxMacroTools

import scala.reflect.macros.blackbox
import scala.language.experimental.macros
import scalanative.unsafe._


sealed trait Block
sealed trait Block0[R] extends Block
sealed trait Block1[T1,R] extends Block
sealed trait Block2[T1,T2,R] extends Block
sealed trait Block3[T1,T2,T3,R] extends Block
sealed trait Block4[T1,T2,T3,T4,R] extends Block
sealed trait Block5[T1,T2,T3,T4,T5,R] extends Block
sealed trait Block6[T1,T2,T3,T4,T5,T6,R] extends Block
sealed trait Block7[T1,T2,T3,T4,T5,T6,T7,R] extends Block
sealed trait Block8[T1,T2,T3,T4,T5,T6,T7,T8,R] extends Block
sealed trait Block9[T1,T2,T3,T4,T5,T6,T7,T8,T9,R] extends Block

object Block {

  protected[objc] type BlockStruct = CStruct6[Ptr[Byte],CInt,CInt,Ptr[Byte],Ptr[Byte],Ptr[Byte]]

  @inline def apply[R](f: CFuncPtr1[Ptr[Byte],R])(implicit ba: BlockAlloc): Block0[R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block0[R]]

  @inline def apply[T1,R](f: CFuncPtr2[Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block1[T1,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block1[T1,R]]

  @inline def apply[T1,T2,R](f: CFuncPtr3[Ptr[Byte],Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block2[T1,T2,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block2[T1,T2,R]]

  @inline def apply[T1,T2,T3,R](f: CFuncPtr4[Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block3[T1,T2,T3,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block3[T1,T2,T3,R]]

  @inline def apply[T1,T2,T3,T4,R](f: CFuncPtr5[Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block4[T1,T2,T3,T4,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block4[T1,T2,T3,T4,R]]

  @inline def apply[T1,T2,T3,T4,T5,R](f: CFuncPtr6[Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block5[T1,T2,T3,T4,T5,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block5[T1,T2,T3,T4,T5,R]]

  @inline def apply[T1,T2,T3,T4,T5,T6,R](f: CFuncPtr7[Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block6[T1,T2,T3,T4,T5,T6,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block6[T1,T2,T3,T4,T5,T6,R]]

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,R](f: CFuncPtr8[Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],Ptr[Byte],R])(implicit ba: BlockAlloc): Block7[T1,T2,T3,T4,T5,T6,T7,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block7[T1,T2,T3,T4,T5,T6,T7,R]]

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,R](f: CFuncPtr9[Block,T1,T2,T3,T4,T5,T6,T7,T8,R])(implicit ba: BlockAlloc): Block8[T1,T2,T3,T4,T5,T6,T7,T8,R] =
    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block8[T1,T2,T3,T4,T5,T6,T7,T8,R]]

//  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,R](f: CFuncPtr10[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,R])(implicit ba: BlockAlloc): Block9[T1,T2,T3,T4,T5,T6,T7,T8,T9,R] =
//    makeBlock(f.asInstanceOf[Ptr[Byte]]).asInstanceOf[Block9[T1,T2,T3,T4,T5,T6,T7,T8,T9,R]]
/*
  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R](f: CFuncPtr11[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R](f: CFuncPtr12[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R](f: CFuncPtr13[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R](f: CFuncPtr14[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R](f: CFuncPtr15[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R](f: CFuncPtr16[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R](f: CFuncPtr17[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R](f: CFuncPtr18[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R](f: CFuncPtr19[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])

  @inline def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R](f: CFuncPtr20[Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R])(implicit ba: BlockAlloc): Block =
    makeBlock(f.asInstanceOf[Ptr[Byte]])
*/
  private def makeBlock(f: Ptr[Byte])(implicit ba: BlockAlloc) : Block = {
    val block = ba.alloc()
  /* TODO:
    !block._1 = null
    !block._2 = 1<<28 // mark block as global
    !block._3 = 0
    !block._4 = f.asInstanceOf[Ptr[Byte]]
    !block._5 = null
    !block._6 = ba.asInstanceOf[Ptr[Byte]] // store the reference to the allocator so that we can use it to release the block

   */
    block.asInstanceOf[Block]
  }

  def apply[R](f: Block=>R): Block = macro Macros.blockImpl
  def apply[T1,R](f: (Block,T1)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,R](f: (Block,T1,T2)=>R): Block2[T1,T2,R] = macro Macros.blockImpl
  def apply[T1,T2,T3,R](f: (Block,T1,T2,T3)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,R](f: (Block,T1,T2,T3,T4)=>R): Block = macro Macros.blockImpl
//  def apply[T1,T2,T3,T4,T5,R](f: (Block,T1,T2,T3,T4,T5)=>R): Block = macro Macros.blockImpl
//  def apply[T1,T2,T3,T4,T5,T6,R](f: (Block,T1,T2,T3,T4,T5,T6)=>R): Block = macro Macros.blockImpl
//  def apply[T1,T2,T3,T4,T5,T6,T7,R](f: (Block,T1,T2,T3,T4,T5,T6,T7)=>R): Block = macro Macros.blockImpl
//  def apply[T1,T2,T3,T4,T5,T6,T7,T8,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8)=>R): Block = macro Macros.blockImpl
//  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9)=>R): Block = macro Macros.blockImpl
  /*
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18)=>R): Block = macro Macros.blockImpl
  def apply[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,R](f: (Block,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19)=>R): Block = macro Macros.blockImpl
*/

  implicit final class Wrapper(val block: Block) {
    def release(): Unit = {
      val ba = block.asInstanceOf[Ptr[BlockStruct]]._6.asInstanceOf[BlockAlloc]
      ba.free(block)
    }
  }

  protected[objc] class Macros(val c: blackbox.Context) extends BlackboxMacroTools with ObjCMacroTools {
    import c.universe._

    def blockImpl(f: Tree): Tree = {
      val vparams = f match {
        case func: Function => func.vparams
        case c.universe.Block(stats, c.universe.Function(vparams, body)) => vparams
        case x => error(s"unsupported Block argument of type: ${x.getClass}"); Nil
      }

      val (callParams, callArgs) = vparams.map(p => (q"${p.name} : scalanative.native.Ptr[Byte]", wrapResult(q"${p.name}" ,p.tpt)) ).unzip
      val call = q"(..$callParams) => $f(..$callArgs)"
//      println(call)
      vparams.size match {
        case 1 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction1($call))"
        case 2 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction2($call))"
        case 3 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction3($call))"
        case 4 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction4($call))"
        case 5 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction5($call))"
        case 6 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction6($call))"
        case 7 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction7($call))"
        case 8 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction8($call))"
        case 9 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction9($call))"
        case 10 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction10($call))"
        case 11 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction11($call))"
        case 12 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction12($call))"
        case 13 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction13($call))"
        case 14 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction14($call))"
        case 15 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction15($call))"
        case 16 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction16($call))"
        case 17 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction17($call))"
        case 18 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction18($call))"
        case 19 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction19($call))"
        case 20 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction20($call))"
        case 21 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction21($call))"
        case 22 =>
          q"scalanative.native.objc.Block(scalanative.native.CFuncPtr.fromFunction22($call))"
      }
    }

  }
}
