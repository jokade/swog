package lua


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
}
