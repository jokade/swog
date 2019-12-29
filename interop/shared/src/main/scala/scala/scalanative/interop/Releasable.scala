package scala.scalanative.interop

trait Releasable {
  def free(): Unit
}
