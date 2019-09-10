package scala.scalanative.cxx

import scala.scalanative.cobj.CObject
import scala.scalanative.interop.Releasable

/**
 * Base class for all external objects represented by a Scala class annotated with @Cxx
 */
trait CxxObject extends CObject {
}
