package scala.scalanative.native

class AutoreleasePool private (private var _allocated: List[Releasable]) {
  def register(obj: Releasable): Unit = _allocated = obj::_allocated
  def releaseAll(): Unit = {
    _allocated.foreach(_.free())
    _allocated = Nil
  }
}

object AutoreleasePool {

  def apply[R](block: AutoreleasePool => R): R = {
    val pool = new AutoreleasePool(Nil)
    val res = block(pool)
    pool.releaseAll()
    res
  }

  def apply(): AutoreleasePool = new AutoreleasePool(Nil)
}