/*
 * mockups.m
 * Copyright (C) 2019 kastner <kastner@dagobah.local>
 *
 * Distributed under terms of the MIT license.
 */

#import <Foundation/Foundation.h>

@interface Number : NSObject
-(int)get;
//@property(assign) int value;
@end

@implementation Number

-(int)get {
  return 42;
}
@end
