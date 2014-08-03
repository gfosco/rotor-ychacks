//
//  PIRAppDelegate.m
//  Pirotor
//
//  Created by Fosco Marotto on 8/2/14.
//  Copyright (c) 2014 fosco. All rights reserved.
//

#import "PIRAppDelegate.h"

@implementation PIRAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

    [Parse setApplicationId:@"vVZDaqOQeTMLeEahIlmQ0w62uwA4LdijG6cf3XmJ" clientKey:@"MWxdk9QPsZscoC8JsPRID9EBFJ6FxJK6iCYPKzNW"];
    [PFUser enableAutomaticUser];
    PFUser *current = [PFUser currentUser];
    [current incrementKey:@"runCount"];
    [current saveEventually];
    return YES;
}
							
- (void)applicationDidBecomeActive:(UIApplication *)application
{
    NSLog(@"Did become active.");
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}


@end
