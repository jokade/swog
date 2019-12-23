/*
 * Test mock-ups for platform/JVM
 */

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

typedef struct _ptestStruct {
  int i;
  char c;
} ptestStruct;

ptestStruct ptest_global_struct = {.i = 42, .c = 'p'};

ptestStruct* ptest_struct_get() {
  return &ptest_global_struct;
}
