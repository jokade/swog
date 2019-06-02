package tests.cobj

import utest._

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
        val list = SList()
        list.isEmpty ==> true
        list.size ==> 0
      }
    }
  }
}
