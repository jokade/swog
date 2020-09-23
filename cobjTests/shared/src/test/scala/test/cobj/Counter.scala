package test.cobj

import de.surfice.smacrotools.debug

import scala.scalanative.cobj.{ResultPtr, _}
import scala.scalanative.unsafe._

@CObj
@debug
class Counter extends Number {
  def increment(): CInt = extern
}

object Counter {
  @name("counter_new")
  def apply(): Counter = extern
  def withStepSize(stepSize: CInt): Counter = extern
}

