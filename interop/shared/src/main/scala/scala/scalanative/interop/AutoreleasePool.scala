package scala.scalanative.interop

import scala.collection.mutable
import scalanative.unsafe._
import scalanative.annotation._

class AutoreleasePool private (private val _allocated: mutable.UnrolledBuffer[Releasable]) {
  def this() = this(mutable.UnrolledBuffer.empty[Releasable])
  def register(obj: Releasable): Unit = _allocated.append(obj)
  def releaseAll(): Unit = {
    _allocated.foreach(_.free())
    _allocated.clear()
  }
}

object AutoreleasePool {

  def apply[R](block: AutoreleasePool => R): R = {
    val pool = new AutoreleasePool()
    val res = block(pool)
    pool.releaseAll()
    res
  }

  def apply(): AutoreleasePool = new AutoreleasePool()
}


