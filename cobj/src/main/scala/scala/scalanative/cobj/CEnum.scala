package scala.scalanative.cobj

abstract class CEnum {
  case class Value(value: Int) {
    def |(or: Value): Value = Value(value | or.value)
    def &(and: Value): Value = Value(value & and.value)
  }
}
