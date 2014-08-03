//
//  PIRAppDelegate.m
//  Pirotor
//
//  Created by Fosco Marotto on 8/2/14.
//  Copyright (c) 2014 fosco. All rights reserved.
//

#import "PIRAppDelegate.h"

@implementation PIRAppDelegate

@synthesize client;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

    [Parse setApplicationId:@"vVZDaqOQeTMLeEahIlmQ0w62uwA4LdijG6cf3XmJ" clientKey:@"MWxdk9QPsZscoC8JsPRID9EBFJ6FxJK6iCYPKzNW"];
    [PFUser enableAutomaticUser];
    PFUser *current = [PFUser currentUser];
    [current incrementKey:@"runCount"];
    [current saveInBackground];
    self.client = [[SISocketIOClient alloc] initWithHost:@"rtrp.io" onPort:80];
    self.client.delegate = self;
    [self.client open];
    return YES;
}
							
- (void)applicationDidBecomeActive:(UIApplication *)application
{
    
}

-(void)socketIOClientOnOpen:(SISocketIOClient *)client{
    NSLog(@"Connected to Socket.IO");
    //[self.client send:[@"[\"alias\",\"pirotor\"]" dataUsingEncoding:NSUTF8StringEncoding]];
}

-(void)socketIOClientOnClose:(SISocketIOClient *)client{
    NSLog(@"Client disconnected.");
}

-(void)socketIOClientOnError:(SISocketIOClient *)client error:(NSError *)error{
    NSLog(error.description);
}

-(void)socketIOClientOnPacket:(SISocketIOClient *)client packet:(SIEngineIOPacket *)packet{
    NSLog(@"What");
    NSLog(packet.message);
}

@end
