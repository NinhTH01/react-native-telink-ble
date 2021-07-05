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

#define KeyBindType                 @"kKeyBindType"

@interface TelinkBle : RCTEventEmitter<RCTBridgeModule>

@property (strong, nonatomic) NSMutableArray <AddDeviceModel *>*source;

@end

#endif /* TelinkBle_h */
