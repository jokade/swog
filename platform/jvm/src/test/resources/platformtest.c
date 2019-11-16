/*
 * Test mock-ups for platform/JVM
 */

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
