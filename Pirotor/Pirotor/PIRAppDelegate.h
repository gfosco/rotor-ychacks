//
//  PIRAppDelegate.h
//  Pirotor
//
//  Created by Fosco Marotto on 8/2/14.
//  Copyright (c) 2014 fosco. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PIRAppDelegate : UIResponder <UIApplicationDelegate, SISocketIOClientDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) SISocketIOClient *client;

@end
