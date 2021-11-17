//
//  TelinkBle+SigMeshDelegate.m
//  TelinkBle
//
//  Created by Thanh Tùng on 09/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TelinkBle+SigMessageDelegate.h"

@implementation TelinkBle (SigMessageDelegate)

- (NSString* _Nullable)getUUIDFromMeshAddress:(UInt16)meshAddress
{
    for (SigNodeModel* node in SigDataSource.share.curNodes)
    {
        if (node.address == meshAddress) {
            return node.UUID;
        }
    }
    return nil;
}

- (void)didReceiveMessage:(SigMeshMessage *)message sentFromSource:(UInt16)source toDestination:(UInt16)destination
{
    if ([message isKindOfClass:[SigTelinkOnlineStatusMessage class]])
    {
        SigTelinkOnlineStatusMessage* msg = (SigTelinkOnlineStatusMessage*) message;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:EVENT_DEVICE_STATUS body:@{
                @"uuid": [self getUUIDFromMeshAddress:source],
                @"meshAddress": [NSNumber numberWithUnsignedShort:source],
                @"online": [NSNumber numberWithBool:msg.state],
            }];
        });
    }
    
    if ([message isKindOfClass:[SigGenericOnOffStatus class]])
    {
        SigGenericOnOffStatus* msg = (SigGenericOnOffStatus*) message;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:EVENT_DEVICE_STATUS body:@{
                @"uuid": [self getUUIDFromMeshAddress:source],
                @"meshAddress": [NSNumber numberWithUnsignedShort:source],
                @"status": [NSNumber numberWithBool:msg.targetState],
            }];
        });
    }
    
    if ([message isKindOfClass:[SigLightLightnessStatus class]])
    {
        SigLightLightnessStatus* msg = (SigLightLightnessStatus*) message;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:EVENT_DEVICE_STATUS body:@{
                @"uuid": [self getUUIDFromMeshAddress:source],
                @"meshAddress": [NSNumber numberWithUnsignedShort:source],
                @"brightness": [NSNumber numberWithUnsignedShort:msg.targetLightness / 0xFFFF * 100],
            }];
        });
    }
    
    if ([message isKindOfClass:[SigLightCTLTemperatureStatus class]])
    {
        SigLightCTLTemperatureStatus* msg = (SigLightCTLTemperatureStatus*) message;
        UInt8 temperature = [LibTools tempToTemp100:msg.presentCTLTemperature];
        NSString* uuid;
        for (SigNodeModel* node in SigDataSource.share.curNodes)
        {
            if (node.temperatureAddresses.count > 0) {
                if ([node.temperatureAddresses[0] unsignedShortValue] == source) {
                    uuid = node.UUID;
                }
            }
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:EVENT_DEVICE_STATUS body:@{
                @"uuid": uuid,
                @"meshAddress": [NSNumber numberWithUnsignedShort:source-1],
                @"temperature": [NSNumber numberWithUnsignedInt:temperature],
            }];
        });
    }
    
    if ([message isKindOfClass:[SigLightHSLStatus class]])
    {
        SigLightHSLStatus* msg = (SigLightHSLStatus*) message;
        double hue        = (double) msg.HSLHue        / 0xFFFF * 360;
        double saturation = (double) msg.HSLSaturation / 0xFFFF * 100;
        double lightness  = (double) msg.HSLLightness  / 0xFFFF * 100;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self sendEventWithName:EVENT_DEVICE_STATUS body:@{
                @"uuid": [self getUUIDFromMeshAddress:source],
                @"meshAddress": [NSNumber numberWithUnsignedShort:source],
                @"hsl": @{
                    @"hue": [NSNumber numberWithDouble:hue],
                    @"saturation": [NSNumber numberWithDouble:saturation],
                    @"lightness": [NSNumber numberWithDouble:lightness],
                },
            }];
        });
    }
}

- (void)didSendMessage:(SigMeshMessage *)message fromLocalElement:(SigElementModel *)localElement toDestination:(UInt16)destination
{
    //
}

- (void)failedToSendMessage:(SigMeshMessage *)message fromLocalElement:(SigElementModel *)localElement toDestination:(UInt16)destination error:(NSError *)error
{
    //
}

- (void)didReceiveSigProxyConfigurationMessage:(SigProxyConfigurationMessage *)message sentFromSource:(UInt16)source toDestination:(UInt16)destination
{
    //
}

@end
