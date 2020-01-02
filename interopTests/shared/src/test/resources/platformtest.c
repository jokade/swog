/*
 * Test mock-ups for platform/JVM
 */
#include <stdlib.h>
#include <limits.h>

void* ptest_return_ptr(void* p) {
  return p;
}

_Bool ptest_return_bool(_Bool b) {
  return b;
}

int ptest_return_char(char c) {
  return c;
}

int ptest_return_int(int i) {
  return i;
}

long ptest_return_long(long l) {
  return l;
}

long ptest_return_long_long(long long l) {
  return l;
}

unsigned char ptest_return_uchar(unsigned char u) {
  return u;
}

unsigned short ptest_return_ushort(unsigned short u) {
  return u;
}

unsigned int ptest_return_uint(unsigned int u) {
  return u;
}

unsigned long ptest_return_ulong(unsigned long u) {
  return u;
}

float ptest_return_float(float f) {
  return f;
}

double ptest_return_double(double d) {
  return d;
}

const char* ptest_return_string(const char *s) {
  return s;
}

typedef struct _ptest_struct1 {
  int i;
} ptest_struct1;

ptest_struct1* ptest_struct1_new() {
  ptest_struct1* s = malloc(sizeof(ptest_struct1));
  s->i = 42;
  return s;
}

typedef struct _ptest_struct2 {
  char c;
  long l;
} ptest_struct2;

ptest_struct2* ptest_struct2_new() {
  ptest_struct2* s = malloc(sizeof(ptest_struct2));
  s->c = 'a';
  s->l = 123456789;
  return s;
}

typedef struct _ptest_struct3 {
  short s;
  char* string;
  int i;
} ptest_struct3;

ptest_struct3* ptest_struct3_new() {
  ptest_struct3* s = malloc(sizeof(ptest_struct3));
  s->s = -4321;
  s->string = "Hello, world!";
  s->i = -1234567; 
  return s;
}

typedef struct _ptest_struct4_sub {
  char x;
} ptest_struct4_sub;

typedef struct _ptest_struct4 {
  char c;
  short s;
  ptest_struct4_sub sub;
  long long l;
} ptest_struct4;

ptest_struct4* ptest_struct4_new() {
  ptest_struct4* s = malloc(sizeof(ptest_struct4));
  s->c = 'c';
  s->s = 99;
  s->sub.x = 'x';
  s->l = LONG_MIN;
  return s;
}

void ptest_incr_int_ptr(int* i) {
  *i += 1;
}

typedef struct _ptest_num_struct {
  long long ll;
  int i;
} ptest_num_struct;

void ptest_incr_num_struct(ptest_num_struct* p) {
  p->ll += 1;
  p->i += 1;
}

int ptest_global_int = 12345678;

typedef int (*func0_t)();

int ptest_call_func0(func0_t f) {
  return f();
}
