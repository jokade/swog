// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 1)
package scala.scalanative.unsafe

import scala.annotation.tailrec
import scala.scalanative.runtime.RawPtr
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

sealed abstract class CStruct

protected[unsafe] class CStructMacroImpl(val c: blackbox.Context) extends MacroTools {
  import c.universe._

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _1[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,0)
  def _1_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,0,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _2[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,1)
  def _2_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,1,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _3[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,2)
  def _3_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,2,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _4[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,3)
  def _4_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,3,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _5[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,4)
  def _5_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,4,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _6[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,5)
  def _6_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,5,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _7[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,6)
  def _7_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,6,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _8[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,7)
  def _8_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,7,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _9[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,8)
  def _9_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,8,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _10[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,9)
  def _10_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,9,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _11[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,10)
  def _11_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,10,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _12[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,11)
  def _12_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,11,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _13[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,12)
  def _13_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,12,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _14[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,13)
  def _14_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,13,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _15[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,14)
  def _15_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,14,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _16[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,15)
  def _16_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,15,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _17[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,16)
  def _17_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,16,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _18[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,17)
  def _18_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,17,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _19[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,18)
  def _19_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,18,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _20[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,19)
  def _20_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,19,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _21[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,20)
  def _21_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,20,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _22[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,21)
  def _22_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,21,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 21)
  def _23[T: c.WeakTypeTag]: c.Tree = genFieldGetter(c.prefix,22)
  def _23_=[T: c.WeakTypeTag](v: c.Tree): c.Tree = genFieldSetter(c.prefix,22,v)
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 24)
  
 



}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct0(val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct1[T1](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct2[T1, T2](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct3[T1, T2, T3](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct4[T1, T2, T3, T4](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct5[T1, T2, T3, T4, T5](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct6[T1, T2, T3, T4, T5, T6](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct7[T1, T2, T3, T4, T5, T6, T7](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct8[T1, T2, T3, T4, T5, T6, T7, T8](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct9[T1, T2, T3, T4, T5, T6, T7, T8, T9](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _17: T17 = macro CStructMacroImpl._17[T17]
  def _17_=(v: T17): Unit = macro CStructMacroImpl._17_=[T17]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _17: T17 = macro CStructMacroImpl._17[T17]
  def _17_=(v: T17): Unit = macro CStructMacroImpl._17_=[T17]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _18: T18 = macro CStructMacroImpl._18[T18]
  def _18_=(v: T18): Unit = macro CStructMacroImpl._18_=[T18]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _17: T17 = macro CStructMacroImpl._17[T17]
  def _17_=(v: T17): Unit = macro CStructMacroImpl._17_=[T17]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _18: T18 = macro CStructMacroImpl._18[T18]
  def _18_=(v: T18): Unit = macro CStructMacroImpl._18_=[T18]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _19: T19 = macro CStructMacroImpl._19[T19]
  def _19_=(v: T19): Unit = macro CStructMacroImpl._19_=[T19]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _17: T17 = macro CStructMacroImpl._17[T17]
  def _17_=(v: T17): Unit = macro CStructMacroImpl._17_=[T17]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _18: T18 = macro CStructMacroImpl._18[T18]
  def _18_=(v: T18): Unit = macro CStructMacroImpl._18_=[T18]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _19: T19 = macro CStructMacroImpl._19[T19]
  def _19_=(v: T19): Unit = macro CStructMacroImpl._19_=[T19]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _20: T20 = macro CStructMacroImpl._20[T20]
  def _20_=(v: T20): Unit = macro CStructMacroImpl._20_=[T20]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _17: T17 = macro CStructMacroImpl._17[T17]
  def _17_=(v: T17): Unit = macro CStructMacroImpl._17_=[T17]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _18: T18 = macro CStructMacroImpl._18[T18]
  def _18_=(v: T18): Unit = macro CStructMacroImpl._18_=[T18]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _19: T19 = macro CStructMacroImpl._19[T19]
  def _19_=(v: T19): Unit = macro CStructMacroImpl._19_=[T19]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _20: T20 = macro CStructMacroImpl._20[T20]
  def _20_=(v: T20): Unit = macro CStructMacroImpl._20_=[T20]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _21: T21 = macro CStructMacroImpl._21[T21]
  def _21_=(v: T21): Unit = macro CStructMacroImpl._21_=[T21]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 87)

final class CStruct22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22](val rawptr: RawPtr) extends CStruct {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _1: T1 = macro CStructMacroImpl._1[T1]
  def _1_=(v: T1): Unit = macro CStructMacroImpl._1_=[T1]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _2: T2 = macro CStructMacroImpl._2[T2]
  def _2_=(v: T2): Unit = macro CStructMacroImpl._2_=[T2]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _3: T3 = macro CStructMacroImpl._3[T3]
  def _3_=(v: T3): Unit = macro CStructMacroImpl._3_=[T3]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _4: T4 = macro CStructMacroImpl._4[T4]
  def _4_=(v: T4): Unit = macro CStructMacroImpl._4_=[T4]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _5: T5 = macro CStructMacroImpl._5[T5]
  def _5_=(v: T5): Unit = macro CStructMacroImpl._5_=[T5]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _6: T6 = macro CStructMacroImpl._6[T6]
  def _6_=(v: T6): Unit = macro CStructMacroImpl._6_=[T6]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _7: T7 = macro CStructMacroImpl._7[T7]
  def _7_=(v: T7): Unit = macro CStructMacroImpl._7_=[T7]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _8: T8 = macro CStructMacroImpl._8[T8]
  def _8_=(v: T8): Unit = macro CStructMacroImpl._8_=[T8]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _9: T9 = macro CStructMacroImpl._9[T9]
  def _9_=(v: T9): Unit = macro CStructMacroImpl._9_=[T9]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _10: T10 = macro CStructMacroImpl._10[T10]
  def _10_=(v: T10): Unit = macro CStructMacroImpl._10_=[T10]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _11: T11 = macro CStructMacroImpl._11[T11]
  def _11_=(v: T11): Unit = macro CStructMacroImpl._11_=[T11]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _12: T12 = macro CStructMacroImpl._12[T12]
  def _12_=(v: T12): Unit = macro CStructMacroImpl._12_=[T12]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _13: T13 = macro CStructMacroImpl._13[T13]
  def _13_=(v: T13): Unit = macro CStructMacroImpl._13_=[T13]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _14: T14 = macro CStructMacroImpl._14[T14]
  def _14_=(v: T14): Unit = macro CStructMacroImpl._14_=[T14]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _15: T15 = macro CStructMacroImpl._15[T15]
  def _15_=(v: T15): Unit = macro CStructMacroImpl._15_=[T15]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _16: T16 = macro CStructMacroImpl._16[T16]
  def _16_=(v: T16): Unit = macro CStructMacroImpl._16_=[T16]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _17: T17 = macro CStructMacroImpl._17[T17]
  def _17_=(v: T17): Unit = macro CStructMacroImpl._17_=[T17]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _18: T18 = macro CStructMacroImpl._18[T18]
  def _18_=(v: T18): Unit = macro CStructMacroImpl._18_=[T18]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _19: T19 = macro CStructMacroImpl._19[T19]
  def _19_=(v: T19): Unit = macro CStructMacroImpl._19_=[T19]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _20: T20 = macro CStructMacroImpl._20[T20]
  def _20_=(v: T20): Unit = macro CStructMacroImpl._20_=[T20]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _21: T21 = macro CStructMacroImpl._21[T21]
  def _21_=(v: T21): Unit = macro CStructMacroImpl._21_=[T21]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 90)
  def _22: T22 = macro CStructMacroImpl._22[T22]
  def _22_=(v: T22): Unit = macro CStructMacroImpl._22_=[T22]
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/CStruct.scala.gyb", line: 93)
}

