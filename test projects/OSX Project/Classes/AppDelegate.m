//
//  AppDelegate.m
//  QuickLookTest
//
//  Created by Sidney Just on 06/01/16.
//  Copyright (c) 2016 Widerwille. All rights reserved.
//


#import "AppDelegate.h"


@interface AppDelegate()
@property (weak) IBOutlet NSWindow *window;
@end

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
	__unused NSImage *image = [NSImage imageNamed:NSImageNameComputer];
	__unused NSBezierPath *path = [NSBezierPath bezierPathWithRoundedRect:NSMakeRect(0, 0, 180, 80) xRadius:16 yRadius:16];
	__unused NSView *view = [_window contentView];
	__unused NSColor *color = [NSColor redColor];

	NSLog(@"Hello World");
}

- (void)applicationWillTerminate:(NSNotification *)aNotification
{
	// Insert code here to tear down your application
}


@end
