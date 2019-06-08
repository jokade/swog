/* Provides functions used to test the CObj interop features */
#include <stdlib.h>

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

/* Callbacks */
typedef int (*Callback0) (void);

int callbacks_exec0(Callback0 f) {
  return f();
}

