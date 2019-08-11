package lua

import scala.scalanative.interop.AutoReleasable

/**
 * High-level interface to a Lua interpreter instance.
 */
trait Lua extends AutoReleasable {
  /**
   * Loads the standard libraries and the scala utils into this instance.
   */
  def init(): Unit

  /**
   * Registers the speficied LuaModule (i.e. it can be accessed from lua via `scala.load()`)
   */
  def registerModule(module: LuaModule): Unit

  /**
   * Registers all specified LuaModules (i.e. they can be accessed from lua via 'scala.load()')
   * @param modules
   */
  def registerModules(modules: LuaModule*): Unit = modules.foreach(registerModule)

  /**
   * Executes the provided string as a chunk in this instance.
   * @param script
   */
  def execString(script: String): Unit

  def execFile(filename: String): Unit

  def isNil(idx: Int): Boolean

  /**
   * Returns the value at the given position of the Lua stack.
   *
   * @param idx stack index (positive values: aboslute index; negative values: position from the top)
   * @return
   */
  def getValue(idx: Int): Any

  /**
   * Returns the value of the specified global variable, or None.
   *
   * @param name
   */
  def getGlobalValue(name: String): Option[Any]

  /**
   * Returns the value at the given stack index as Boolean.
   *
   * @param idx
   */
  def boolean(idx: Int): Boolean
  @inline final def booleanOption(idx: Int): Option[Boolean] = if(isNil(idx)) None else Some(boolean(idx))

  /**
   * Returns the value at the given stack index as Int.
   *
   * @param idx
   */
  def int(idx: Int): Int
  @inline final def intOption(idx: Int): Option[Int] = if(isNil(idx)) None else Some(int(idx))

  /**
   * Returns the value at the given stack index as Long.
   *
   * @param idx
   */
  def long(idx: Int): Long
  @inline final def longOption(idx: Int): Option[Long] = if(isNil(idx)) None else Some(long(idx))

  /**
   * Returns the value at the given stack index as Float.
   *
   * @param idx
   */
  def float(idx: Int): Float
  @inline final def floatOption(idx: Int): Option[Float] = if(isNil(idx)) None else Some(float(idx))

  /**
   * Returns the value at the given stack index as Double.
   *
   * @param idx
   */
  def double(idx: Int): Double
  @inline final def doubleOption(idx: Int): Option[Double] = if(isNil(idx)) None else Some(double(idx))

  /**
   * Returns the value at the given stack index as String.
   *
   * @param idx
   */
  def string(idx: Int): String
  @inline final def stringOption(idx: Int): Option[String] = if(isNil(idx)) None else Some(string(idx))

  def table(idx: Int): LuaTable

}

object Lua {
  def apply(): Lua = LuaState()
}
