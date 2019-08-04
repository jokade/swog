package lua

trait LuaWrapper {
  /**
   * Pushes this object as user data onto the specified LuaState's stack and associates it with the corresponding metatable
   *
   * @param state
   */
  protected[lua] def __luaPush(state: LuaState): Unit
}
