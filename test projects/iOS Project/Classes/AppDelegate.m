//
//  AppDelegate.m
//  QuickLookTest
//
//  Created by Sidney Just on 04/01/16.
//  Copyright (c) 2016 Widerwille. All rights reserved.
//


#import <CoreLocation/CoreLocation.h>
#import "AppDelegate.h"

@interface AppDelegate()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
	UISlider *slider = [[UISlider alloc] initWithFrame:CGRectMake(0, 0, 80, 30)];
	[slider setMinimumValue:0.0];
	[slider setMaximumValue:1.0];
	[slider setValue:0.8];

	__unused UIColor *color1 = [UIColor redColor];
	__unused UIColor *color2 = [UIColor colorWithRed:0.2 green:1.0 blue:0.3 alpha:0.8];

	__unused UIImage *image1 = [UIImage imageNamed:@"plaster2.png"];
	__unused UIImage *image2 = [UIImage imageNamed:@"rock.png"];
	__unused UIImage *image3 = [UIImage imageNamed:@"flowermap.png"];

	__unused NSURL *url = [NSURL URLWithString:@"http://google.com"];
	__unused CLLocation *location = [[CLLocation alloc] initWithLatitude:37.331688 longitude:-122.030789];

	__unused UIBezierPath *path = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(0, 0, 100, 100) cornerRadius:16];

	return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
	// Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
	// Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.

}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
	// Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
	// If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.

}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
	// Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
	// Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
	// Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


@end
