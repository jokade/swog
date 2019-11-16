package scala.scalanative.unsafe

import com.sun.jna.{Native, NativeMapped, Pointer}

sealed abstract class CStruct {
  def unary_![T](): T = ???
}
object CStruct {
}

abstract class CStruct1[T1](val p: Ptr[CStruct1[T1]]) extends CStruct {
  def _1: T1 = ???
}

abstract class CStruct2[T1,T2](var _1: T1, var _2: T2) extends CStruct {
  def this() = this(
    null.asInstanceOf[T1],
    null.asInstanceOf[T2])
}

abstract class CStruct3[T1,T2,T3](var _1: T1, var _2: T2, var _3: T3) extends CStruct {
  def this() = this(
    null.asInstanceOf[T1],
    null.asInstanceOf[T2],
    null.asInstanceOf[T3])
}

abstract class CStruct4[T1,T2,T3,T4](var _1: T1, var _2: T2, var _3: T3, var _4: T4) extends CStruct {
  def this() = this(
    null.asInstanceOf[T1],
    null.asInstanceOf[T2],
    null.asInstanceOf[T3],
    null.asInstanceOf[T4])
}

abstract class CStruct5[T1,T2,T3,T4,T5](var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5) extends CStruct {
  def this() = this(
    null.asInstanceOf[T1],
    null.asInstanceOf[T2],
    null.asInstanceOf[T3],
    null.asInstanceOf[T4],
    null.asInstanceOf[T5])
}

abstract class CStruct6[T1,T2,T3,T4,T5,T6](var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5, var _6: T6) extends CStruct {
  def this() = this(
    null.asInstanceOf[T1],
    null.asInstanceOf[T2],
    null.asInstanceOf[T3],
    null.asInstanceOf[T4],
    null.asInstanceOf[T5],
    null.asInstanceOf[T6])
}
