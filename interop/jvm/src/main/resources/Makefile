CFLAGS+= -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/darwin

libswog.dylib: swog.c
	$(CC) -ObjC $(CFLAGS) -shared -framework Foundation -o libswog.dylib swog.c

clean:
	rm -f libswog.dylib

.PHONY: clean
