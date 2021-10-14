#import <TelinkSigMeshLib/TelinkSigMeshLib.h>
#import <UIKit/UIImage.h>
#import <React/RCTBridgeModule.h>
#import "TelinkBle.h"
#import "DemoCommand.h"
#import "NSString+extension.h"
#import "NSData+Conversion.h"
#import "UIImage+Extension.h"

@implementation TelinkBle

RCT_EXPORT_MODULE(TelinkBle)

RCT_EXTERN_METHOD(getNodes:(RCTPromiseResolveBlock _Nonnull)resolve withRejecter:(RCTPromiseRejectBlock _Nonnull)reject)

RCT_EXTERN_METHOD(sendRawString:(nonnull NSString*)command)

RCT_EXTERN_METHOD(startScanning)

RCT_EXTERN_METHOD(stopScanning)

RCT_EXTERN_METHOD(autoConnect)

RCT_EXTERN_METHOD(setStatus:(nonnull NSNumber*)meshAddress withStatus:(nonnull NSNumber*)status)

RCT_EXTERN_METHOD(setBrightness:(nonnull NSNumber*)meshAddress withBrightness:(nonnull NSNumber*)brightness)

RCT_EXTERN_METHOD(setTemperature:(nonnull NSNumber*)meshAddress withTemperature:(nonnull NSNumber*)temperature)

RCT_EXTERN_METHOD(setHSL:(nonnull NSNumber*)meshAddress withHSL:(nonnull NSDictionary*)hsl)

RCT_EXTERN_METHOD(addDeviceToGroup:(nonnull NSNumber*)deviceAddress withGroupAddress:(nonnull NSNumber*)groupAddress)

RCT_EXTERN_METHOD(removeDeviceFromGroup:(nonnull NSNumber*)deviceAddress withGroupAddress:(nonnull NSNumber*)groupAddress)

RCT_EXTERN_METHOD(resetNode:(nonnull NSNumber*)deviceAddress);

RCT_EXTERN_METHOD(setDelegateForIOS)

RCT_EXTERN_METHOD(getOnlineState)

RCT_EXTERN_METHOD(shareQRCode:(nonnull NSString*)path withResolver:(nonnull RCTPromiseResolveBlock)resolve withRejecter:(nonnull RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(startAddingAllDevices)

- (NSDictionary*)getJSModel:(SigScanRspModel*)scanModel
{
    NSData* manufacturerData = (NSData*) [scanModel.advertisementData valueForKey:@"kCBAdvDataManufacturerData"];
    NSString* manufacturerDataString = [manufacturerData hexadecimalString];
    return @{
        @"meshAddress": [NSNumber numberWithUnsignedShort:[scanModel address]],
        @"macAddress": [scanModel macAddress],
        @"uuid": [scanModel uuid],
        @"manufacturerData": manufacturerDataString,
        @"provisioned": [NSNumber numberWithBool:[scanModel provisioned]],
        @"deviceType": [manufacturerDataString substringWithRange:NSMakeRange(54, 8)],
    };
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[
        EVENT_MESH_NETWORK_CONNECTION,
        // Unprovisioned devices
        EVENT_DEVICE_FOUND,
        EVENT_SCANNING_TIMEOUT,
        EVENT_NEW_DEVICE_ADDED,
        // Device provisioning events
        EVENT_PROVISIONING_SUCCESS,
        EVENT_PROVISIONING_FAILED,
        // Device binding events
        EVENT_BINDING_SUCCESS,
        EVENT_BINDING_FAILED,
        // Group setting events
        EVENT_SET_GROUP_SUCCESS,
        EVENT_SET_GROUP_FAILED,
        // Scene setting events
        EVENT_SET_SCENE_SUCCESS,
        EVENT_SET_SCENE_FAILED,
        // Device status
        EVENT_DEVICE_STATUS,
        // Node reset
        EVENT_NODE_RESET_SUCCESS,
        EVENT_NODE_RESET_FAILED,
    ];
}

@end
