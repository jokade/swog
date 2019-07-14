package tests.cobj

import utest._

import scala.scalanative.runtime.Intrinsics
import scala.scalanative.unsafe._
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
        number.self().getValue() ==> 42
        number ==> number.self()
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
        list3.asInstanceOf[SList[Counter]].itemAt(0) match {
          case c: Counter => c.increment()
        }
        list3.itemAt(0).getValue() ==> 2
        list3.itemAt(1).getValue() ==> 42
      }
    }

    'Callbacks-{
      val cb = new CFuncPtr0[Int] {
        override def apply(): Int = 42
      }

      val cb1 = new CFuncPtr1[Int,Int] {
        override def apply(i: Int): Int = i
      }

      Callbacks.exec0(cb) ==> 42
      Callbacks.exec1(cb1,43) ==> 43
    }

    'OutArgs-{
      'Int- {
        implicit val out = ResultPtr.stackalloc[Int]
        OutArgs.int()
        out.value ==> 42
      }
      'Long-{
        implicit val out = ResultPtr.stackalloc[Long]
        OutArgs.long()
        out.value ==> Long.MaxValue
      }
      'Double-{
        implicit val out = ResultPtr.stackalloc[Double]
        OutArgs.double()
        out.value ==> Double.MaxValue
      }
      'StructByValue-{
        implicit val out = ResultPtr.stackalloc[OutArgs.OutStruct]
        OutArgs.struct()
        out.value._1 ==> 42
      }
      'CObject-{
        implicit val out = ResultPtr.stackalloc[Number]
        OutArgs.number()
        out.wrappedValue.getValue() ==> 42
      }
      'alloc-{
        Zone{ implicit z =>
          implicit val intRes = ResultPtr.alloc[Int]
          implicit val objRes = ResultPtr.alloc[Number]
          OutArgs.int()
          intRes.value ==> 42
          OutArgs.number()
          objRes.wrappedValue.getValue() ==> 42
        }
      }
    }

  }
}
