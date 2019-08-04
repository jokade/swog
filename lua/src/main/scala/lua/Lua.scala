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
}

object Lua {
  def apply(): Lua = LuaState()
}
