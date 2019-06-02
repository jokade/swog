package tests.cobj

import utest._
import scalanative.cobj._

object CObjTests extends TestSuite {
  val tests = Tests {
    'SimpleObject - {
      'Number - {
        // The C implementation of Number uses snake case function names, i.e. number_get_value
        val number = Number()
        number.getValue() ==> 0
        number.setValue(42)
        number.getValue() ==> 42
      }
    }


    'Inheritance - {
      'Counter - {
        val counter = Counter.withStepSize(2)
        counter.getValue() ==> 0
        counter.increment() ==> 2
        counter.getValue() ==> 2
      }
    }

    'Generics-{
      'SList-{
        val list = SList[Number]()
        list.isEmpty ==> true
        list.size ==> 0

        val number = Number()
        number.setValue(42)

        val list2 = list.prepend(number)
        list2.isEmpty ==> false
        list2.size ==> 1
        list.isEmpty ==> true
        list.size ==> 0
        list.itemAt(0) ==> null
        list2.itemAt(0).getValue() ==> 42

        val counter = Counter.withStepSize(2)
        val list3 = list2.prepend(counter)
        list3.size ==> 2
        list3.itemAt(0)(defaultWrapper[Counter]) match {
          case c: Counter => c.increment()
        }
        list3.itemAt(0).getValue() ==> 2
        list3.itemAt(1).getValue() ==> 42
      }
    }
  }
}
