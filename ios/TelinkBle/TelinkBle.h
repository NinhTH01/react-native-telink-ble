//
//  TelinkBle.h
//  TelinkBleExample
//
//  Created by Thanh Tùng on 02/07/2021.
//

#ifndef TelinkBle_h
#define TelinkBle_h

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <TelinkSigMeshLib/SigModel.h>

#define EVENT_DEVICE_FOUND          @"EVENT_DEVICE_FOUND"
#define EVENT_SCANNING_TIMEOUT      @"EVENT_SCANNING_TIMEOUT"
#define EVENT_PROVISIONING_START    @"EVENT_PROVISIONING_START"
#define EVENT_PROVISIONING_SUCCESS  @"EVENT_PROVISIONING_SUCCESS"
#define EVENT_PROVISIONING_FAILED   @"EVENT_PROVISIONING_FAILED"
#define EVENT_BINDING_START         @"EVENT_BINDING_START"
#define EVENT_BINDING_SUCCESS       @"EVENT_BINDING_SUCCESS"
#define EVENT_BINDING_FAILED        @"EVENT_BINDING_FAILED"
#define EVENT_SET_GROUP_SUCCESS     @"EVENT_SET_GROUP_SUCCESS"
#define EVENT_SET_GROUP_FAILED      @"EVENT_SET_GROUP_FAILED"
#define EVENT_SET_SCENE_SUCCESS     @"EVENT_SET_SCENE_SUCCESS"
#define EVENT_SET_SCENE_FAILED      @"EVENT_SET_SCENE_FAILED"
#define EVENT_SET_TRIGGER_SUCCESS   @"EVENT_SET_TRIGGER_SUCCESS"
#define EVENT_SET_TRIGGER_FAILED    @"EVENT_SET_TRIGGER_FAILED"
#define EVENT_RESET_NODE_SUCCESS    @"EVENT_RESET_NODE_SUCCESS"
#define EVENT_RESET_NODE_FAILED     @"EVENT_RESET_NODE_FAILED"
#define EVENT_MESH_CONNECT_SUCCESS  @"EVENT_MESH_CONNECT_SUCCESS"
#define EVENT_MESH_CONNECT_FAILED   @"EVENT_MESH_CONNECT_FAILED"
#define EVENT_DEVICE_RESPONSE       @"EVENT_DEVICE_RESPONSE"
#define EVENT_BLE_SDK_BUSY          @"EVENT_BLE_SDK_BUSY"

#define kShareWithBluetoothPointToPoint (YES)
#define kShowScenes                     (YES)
#define kShowDebug                      (NO)
#define kshowLog                        (YES)
#define kshowShare                      (YES)
#define kshowMeshInfo                   (YES)
#define kshowChooseAdd                  (YES)

#define kKeyBindType                        @"kKeyBindType"
#define kRemoteAddType                      @"kRemoteAddType"
#define kFastAddType                        @"kFastAddType"
#define kDLEType                            @"kDLEType"
#define kGetOnlineStatusType                @"kGetOnlineStatusType"
#define kAddStaticOOBDevcieByNoOOBEnable    @"kAddStaticOOBDevcieByNoOOBEnable"
#define KeyBindType                         @"kKeyBindType"
#define kDLEUnsegmentLength                 (229)

@interface TelinkBle : RCTEventEmitter<RCTBridgeModule>

@property (strong, nonatomic) NSMutableArray<AddDeviceModel*> * _Nonnull source;

- (BOOL)isBusy;

- (void)startMeshSDK;

- (void)updateDeviceProvisionSuccess:(NSString *_Nonnull)uuid address:(UInt16)address;

- (void)updateDeviceProvisionFail:(NSString *_Nonnull)uuid;

- (void)updateDeviceKeyBind:(NSString *_Nonnull)uuid address:(UInt16)address isSuccess:(BOOL)isSuccess;

- (void)addDeviceFinish;

- (void)setOnOff:(int)address onOff:(int)onOff;

- (void)setAllOn;

- (void)setAllOff;

- (void)setLuminance:(nonnull NSNumber*)address withLuminance:(nonnull NSNumber*)luminance;

- (void)setTemp:(nonnull NSNumber*)address withTemp:(nonnull NSNumber*)temperature;

- (void)setHsl:(nonnull NSNumber*)address withHSL:(nonnull NSDictionary*)hsl;

- (void)autoConnect;

- (void)stopScanning;

- (void)startScanning;

- (void)getNodes:(RCTPromiseResolveBlock _Nonnull )resolve withRejecter:(RCTPromiseRejectBlock _Nonnull )reject;

- (void)kickOut:(nonnull NSNumber*)address;

- (void)forceRemoveNodeAtAddress:(nonnull NSNumber*)address;

- (void)resetBle;

- (void)addDeviceToGroup:(nonnull NSNumber*)groupId withDeviceId:(nonnull NSNumber*)deviceId;

- (void)removeDeviceFromGroup:(nonnull NSNumber*)groupId withDeviceId:(nonnull NSNumber*)deviceId;

- (void)setSceneForDevice:(nonnull NSNumber*)sceneId withDeviceId:(nonnull NSNumber*)deviceId;

- (void)removeSceneFromDevice:(nonnull NSNumber*)sceneId withDeviceId:(nonnull NSNumber*)deviceId;

- (void)triggerScene:(nonnull NSNumber*)sceneId;

@end

#endif /* TelinkBle_h */
