import de.surfice.smacrotools.debug

import scala.scalanative.native.CObj.{CRef, Out}
import scalanative.native._
import CObj.implicits._

object Main {

  def main(args: Array[String]): Unit = Zone{ implicit z: Zone =>

  }

}

@CObj
class Foo

trait X
trait Y

@CObj
@debug
class Bar(__ref: Ptr[Byte]) extends Foo

//@CObj
//abstract class X

//@CObj
//class Y extends X
