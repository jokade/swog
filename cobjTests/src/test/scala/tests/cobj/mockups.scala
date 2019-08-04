package tests.cobj

import de.surfice.smacrotools.debug

import scala.scalanative.annotation.InlineSource
import scalanative.cobj.{ResultPtr, _}
import scalanative.unsafe._

@CObj
@InlineSource("C",
"""
#include <stdlib.h>

typedef struct {
  int value;
} Number;

Number* number_new() {
  Number *c = malloc(sizeof(Number));
  c->value = 0;
  return c;
}

void number_free(Number* c) {
  free(c);
}

int number_get_value(Number* c) {
  return c->value;
}

void number_set_value(Number* c, int value) {
  c->value = value;
}

Number* number_self(Number* c) {
  return c;
}
""")
class Number {
  def getValue(): CInt = extern
  def setValue(value: CInt): Unit = extern
  def free(): Unit = extern
  @returnsThis
  def self(): this.type = extern
}

object Number {
  @name("number_new")
  def apply(): Number = extern
}


@CObj
@InlineSource("C",
"""
#include <stdlib.h>

typedef struct {
  int value;
  int stepSize;
} Counter;

Counter* counter_new() {
  Counter *c = malloc(sizeof(Counter));
  c->value = 0;
  c->stepSize = 1;
  return c;
}

Counter* counter_with_step_size(int stepSize) {
  Counter *c = counter_new();
  c->stepSize = stepSize;
  return c;
}

int counter_increment(Counter* c) {
  c->value += c->stepSize;
  return c->value;
}
""")
class Counter extends Number {
  def increment(): CInt = extern
}

object Counter {
  @name("counter_new")
  def apply(): Counter = extern
  def withStepSize(stepSize: CInt): Counter = extern
}

@CObj(prefix = "slist_")
@InlineSource("C",
"""
#include <stdlib.h>

typedef struct _SListEntry {
  void* data;
  struct _SListEntry *next;
} SListEntry;

typedef SListEntry SList;

SList* slist_new() {
  SList *list = calloc(1,sizeof(SList));
  return list;
}

int slist_is_empty(SList *l) {
  return (NULL == l) || (NULL == l->data);
}

int slist_size(SList *l) {
  if(slist_is_empty(l)) {
    return 0;
  }
  SListEntry *p = l;
  int size = 1;
  while((NULL != p->next)) {
    p = p->next;
    if(NULL != p->data) {
      size += 1;
    }
    else {
      break;
    }
  }
  return size;
}

SList* slist_prepend(SList *l, void* value) {
  SListEntry *entry = slist_new();
  entry->data = value;
  entry->next = l;
  return entry;
}

void* slist_item_at(SList *l, int index) {
  if(NULL == l || index<0) {
    return NULL;
  }
  SListEntry *p = l;
  for(int i=0; i<index; i++) {
    if(NULL != p->next) {
      p = p->next;
    }
    else {
      return NULL;
    }
  }
  return p->data;
}
""")
class SList[T] {
  def isEmpty: Boolean = extern
  def size: Int = extern
  def prepend(value: T)(implicit wrapper: CObjectWrapper[T]): SList[T] = extern

  // returns null if the specified index does not exist
  @nullable
  def itemAt(index: Int)(implicit wrapper:CObjectWrapper[T]): T = extern

}

object SList {
  @name("slist_new")
  def apply[T<:CObject](): SList[T] = extern
}


@CObj
@InlineSource("C",
"""
typedef int (*Callback0) (void);
typedef int (*Callback1) (int);

int callbacks_exec0(Callback0 f) {
  return f();
}

int callbacks_exec1(Callback1 f, int i) {
  return f(i);
}
""")
object Callbacks {
  def exec0(f: CFuncPtr0[Int]): Int = extern
  def exec1(f: CFuncPtr1[Int,Int], i: Int): Int = extern
}

class NumberLike(val __ptr: Ptr[Byte]) extends CObject

@CObj
@InlineSource("C",
"""
#include <limits.h>
#include <float.h>
#include <stdlib.h>

void implicit_args_int(int* out) {
  *out = 42;
}

void implicit_args_long(long* out) {
  *out = LONG_MAX;
}

void implicit_args_double(double* out) {
  *out = DBL_MAX;
}

typedef struct {
  int foo;
} OutStruct;


void implicit_args_struct(OutStruct* out) {
  out->foo = 42;
}

typedef struct {
  int value;
} OutClass;


void implicit_args_number(OutClass** out) {
  *out = malloc(sizeof(OutClass));
  (*out)->value = 42;
}

int implicit_args_multi_args(OutClass* num1, OutClass* num2) {
  return num1->value + num2->value;
}
""")
@debug
object ImplicitArgs {
  type OutStruct = CStruct1[Int]

  def int()(implicit out: ResultPtr[Int]): Unit = extern
  def long()(implicit out: ResultPtr[Long]): Unit = extern
  def double()(implicit out: ResultPtr[Double]): Unit = extern
  def struct()(implicit out: ResultPtr[OutStruct]): Unit = extern
  def number()(implicit out: ResultPtr[Number]): Unit = extern
  def multiArgs()(implicit num1: Number, num2: NumberLike): Int = extern
}


