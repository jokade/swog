/* Provides functions used to test the CObj interop features */
#include <stdlib.h>
#include <limits.h>
#include <float.h>

/* Number */
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


/* Counter */
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


/* Singly Linked List */
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
//    printf("  p->next: 0x%x    p->data: 0x%x    p->next->data: 0x%x\n",p->next,p->data,p->next->data);
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
  Number *n = (Number*)value;
//  printf("slist_prepend: l=0x%x    value=0x%x\n",l,value);
  SListEntry *entry = slist_new();
  entry->data = value;
  entry->next = l;
//  printf("  entry: 0x%x    data: 0x%x    next: 0x%x\n",entry,entry->data,entry->next);
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

/* Callbacks */
typedef int (*Callback0) (void);
typedef int (*Callback1) (int);

int callbacks_exec0(Callback0 f) {
  return f();
}

int callbacks_exec1(Callback1 f, int i) {
  return f(i);
}

/* implicit args */

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

/* OutArgs */
void out_args_int(int* out) {
  *out = 42;
}

//void out_args_long(long* out) {
//  *out = LONG_MAX;
//}
