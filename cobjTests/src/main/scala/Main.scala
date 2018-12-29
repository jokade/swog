import de.surfice.smacrotools.debug

import scalanative.native._
import cobj._

object Main {

  def main(args: Array[String]): Unit = Zone{ implicit z: Zone =>

  }

}

@CObj
class Bar

@CObj
class Foo {
  def bar(b: Bar): Unit = extern
}

