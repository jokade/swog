/*
 * Helper library for swog-cobj @ JVM
 *
 * Adopted from Rococoa: https://github.com/iterate-ch/rococoa/blob/master/rococoa/rococoa-core/src/main/native/Rococoa.m
 */

#import <Cocoa/Cocoa.h>
#include <objc/objc-runtime.h>

void swog_call_on_main_thread(void (*fn)(), BOOL waitUntilDone) {
  if( waitUntilDone ) {
    dispatch_sync(dispatch_get_main_queue(),^{ fn(); });
  }
  else {
    dispatch_async(dispatch_get_main_queue(),^{ fn(); });
  }
}


