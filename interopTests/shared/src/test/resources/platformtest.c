/*
 * Test mock-ups for platform/JVM
 */
#include <stdlib.h>
#include <limits.h>

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
