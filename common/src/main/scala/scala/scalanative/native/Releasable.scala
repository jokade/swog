package scala.scalanative.native

trait Releasable {
  def free(): Unit
}
