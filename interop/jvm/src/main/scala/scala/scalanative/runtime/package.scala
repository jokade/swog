package scala.scalanative

import com.sun.jna.Pointer

package object runtime {

  type RawPtr = Pointer
  
  object Intrinsics {
    def castObjectToRawPtr(o: Object): RawPtr = o.asInstanceOf[RawPtr]
  }
}
