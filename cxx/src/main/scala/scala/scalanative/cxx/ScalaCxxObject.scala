//     Project: swog
//      Module:
// Description:
package scala.scalanative.cxx

import scala.scalanative._
import unsafe._
import runtime._

trait ScalaCxxObject extends CxxObject {
  def ___setWrapper(w: RawPtr): Unit
  ___setWrapper(Intrinsics.castObjectToRawPtr(this))
}
