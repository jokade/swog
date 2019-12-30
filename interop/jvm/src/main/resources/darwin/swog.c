/*
 * Helper library for swog/JVM
 */

#include <jni.h>

#ifdef __APPLE__
#import <Cocoa/Cocoa.h>
#include <objc/objc-runtime.h>
#endif

// Returns the address of the specified byte array
JNIEXPORT jlong JNICALL Java_scala_scalanative_interop_jvm_JNI_getByteArrayAddress(JNIEnv * env, jclass cls, jbyteArray arr) {
  return (jlong)(*env)->GetByteArrayElements(env,arr,JNI_FALSE);
}

#ifdef __APPLE__
void swog_call_on_main_thread(void (*fn)(), BOOL waitUntilDone) {
  if( waitUntilDone ) {
    dispatch_sync(dispatch_get_main_queue(),^{ fn(); });
  }
  else {
    dispatch_async(dispatch_get_main_queue(),^{ fn(); });
  }
}
#endif


