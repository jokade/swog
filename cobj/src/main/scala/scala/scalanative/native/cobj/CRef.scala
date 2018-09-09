package scala.scalanative.native.cobj

trait CRef[T] {
  def __ref: Ref[T]
}
