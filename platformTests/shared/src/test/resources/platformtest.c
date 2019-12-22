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
