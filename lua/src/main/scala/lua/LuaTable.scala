package lua

import scala.collection.mutable


final class LuaTable protected[lua](state: LuaState, idx: Int) {
  private var offset = idx+1

  @inline private def value(v: Any): Option[Any] = {
    state.pop(1)
    Some(v)
  }

  def get(key: String): Option[Any] = {
    state.getField(idx,key) match {
      case LuaType.NUMBER =>
        if(state.isInteger(offset))
          value( state.getInteger(offset) )
        else
          value( state.getNumber(offset) )
      case LuaType.STRING =>
        value( state.getString(offset) )
      case LuaType.BOOLEAN =>
        value( state.getBoolean(offset) )
      case LuaType.TABLE =>
        offset += 1
        Some( state.table(idx+1) )
      case x =>
        None
    }
  }

  @inline def getOrElse[T](key: String, default: => T): T =  get(key).getOrElse(default).asInstanceOf[T]

  def apply(key: String): Any = get(key).get

  def toMap(): Map[String,Any] = {
    def loop(idx: Int): Map[String,Any] = {
      val map = mutable.Map.empty[String,Any]
      // see https://www.lua.org/manual/5.3/manual.html#lua_next
      state.pushNil()
      while(state.next(idx) != 0) {
        val key = state.getString(-2)
        val value = state.getType(-1) match {
          case LuaType.BOOLEAN =>
            state.getBoolean(-1)
          case LuaType.NUMBER =>
            if(state.isInteger(-1))
              state.getInteger(-1)
            else
              state.getNumber(-1)
          case LuaType.STRING =>
            state.getString(-1)
          case LuaType.TABLE =>
            loop(state.getTop)
//          case LuaType.USERDATA =>
//            state.toUserData(-1)
          case x => null
        }
        state.pop(1)
        map += key -> value
      }
      map.toMap
    }
    loop(idx)
  }
}
