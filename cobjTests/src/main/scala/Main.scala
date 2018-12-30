import de.surfice.smacrotools.debug

import scalanative.native._
import cobj._
import scala.scalanative.native.cobj.runtime.CObjObject

object Main {

  def main(args: Array[String]): Unit = Zone{ implicit z: Zone =>
  }
}

@CObj
@debug
class GError {
  def foo: GError = extern

}
