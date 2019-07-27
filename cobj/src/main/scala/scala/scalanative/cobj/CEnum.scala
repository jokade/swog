package scala.scalanative.cobj

abstract class CEnum {
  class Value(val value: Int) {
    def |(or: Value): Value = new Value(value | or.value)
    def &(and: Value): Value = new Value(value & and.value)

    override def hashCode(): Int = value
    override def equals(other: Any): Boolean = other match {
      case that: Value => this.value == that.value
      case _ => false
    }
  }
  def Value(v: Int): Value = new Value(v)
}


