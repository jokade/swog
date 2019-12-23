// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 1)
package scala.scalanative.unsafe

import com.sun.jna.{FromNativeContext, NativeMapped, Pointer}

import scala.reflect.runtime.universe._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.scalanative.runtime.RawPtr

final class Ptr[T] (protected[scalanative] var rawptr: RawPtr) extends NativeMapped {
  def this() = this(null)
  override def fromNative(nativeValue: Any, context: FromNativeContext): AnyRef = {
    rawptr = nativeValue.asInstanceOf[RawPtr]
    this
  }
  override def toNative: AnyRef = rawptr.asInstanceOf[AnyRef]
  override def nativeType(): Class[_] = classOf[RawPtr]

  def unary_!(): T = macro Ptr.MacroImpl.unary_!
}
object Ptr {
  implicit def ptrToCStruct[T <: CStruct](ptr: Ptr[T]): T = macro MacroImpl.ptrToCStruct[T]
  
  private class MacroImpl(val c: blackbox.Context) {
    import c.universe._
    
    def unary_!(): c.Tree = {
      println("CALLED")
      println(c.prefix)
      q"???"
    }
    
    def ptrToCStruct[T: c.WeakTypeTag](ptr: c.Tree): c.Tree = {
      val tpe = c.weakTypeOf[T]
      tpe.typeArgs match {
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1) => q"new scalanative.unsafe.CStruct1[$t1]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2) => q"new scalanative.unsafe.CStruct2[$t1, $t2]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3) => q"new scalanative.unsafe.CStruct3[$t1, $t2, $t3]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4) => q"new scalanative.unsafe.CStruct4[$t1, $t2, $t3, $t4]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5) => q"new scalanative.unsafe.CStruct5[$t1, $t2, $t3, $t4, $t5]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6) => q"new scalanative.unsafe.CStruct6[$t1, $t2, $t3, $t4, $t5, $t6]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7) => q"new scalanative.unsafe.CStruct7[$t1, $t2, $t3, $t4, $t5, $t6, $t7]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8) => q"new scalanative.unsafe.CStruct8[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9) => q"new scalanative.unsafe.CStruct9[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10) => q"new scalanative.unsafe.CStruct10[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) => q"new scalanative.unsafe.CStruct11[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12) => q"new scalanative.unsafe.CStruct12[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13) => q"new scalanative.unsafe.CStruct13[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14) => q"new scalanative.unsafe.CStruct14[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15) => q"new scalanative.unsafe.CStruct15[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16) => q"new scalanative.unsafe.CStruct16[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15, $t16]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17) => q"new scalanative.unsafe.CStruct17[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15, $t16, $t17]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18) => q"new scalanative.unsafe.CStruct18[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15, $t16, $t17, $t18]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19) => q"new scalanative.unsafe.CStruct19[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15, $t16, $t17, $t18, $t19]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20) => q"new scalanative.unsafe.CStruct20[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15, $t16, $t17, $t18, $t19, $t20]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 32)
        case List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21) => q"new scalanative.unsafe.CStruct21[$t1, $t2, $t3, $t4, $t5, $t6, $t7, $t8, $t9, $t10, $t11, $t12, $t13, $t14, $t15, $t16, $t17, $t18, $t19, $t20, $t21]($ptr.rawptr)"
// ###sourceLocation(file: "/Users/kastner/dev/sn/swog/platform/jvm/src/main/scala/scala/scalanative/unsafe/Ptr.scala.gyb", line: 34)
      }
    }
  }
  
}

