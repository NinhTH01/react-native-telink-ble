//
//  TelinkBle.m
//  TelinkBleExample
//
//  Created by Thanh Tùng on 02/07/2021.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
#import <TelinkSigMeshLib/TelinkSigMeshLib.h>
#import "TelinkBle.h"
#import "ProvisioningDevice.h"
#import "AddDeviceStateModel.h"
#import "DemoCommand.h"

@implementation TelinkBle

- (NSArray<NSString *> *)supportedEvents {
  return @[
    EVENT_DEVICE_FOUND,
    EVENT_SCANNING_TIMEOUT,
    EVENT_PROVISIONING_START,
    EVENT_PROVISIONING_SUCCESS,
    EVENT_PROVISIONING_FAILED,
    EVENT_BINDING_START,
    EVENT_BINDING_SUCCESS,
    EVENT_BINDING_FAILED,
    EVENT_SET_GROUP_SUCCESS,
    EVENT_SET_GROUP_FAILED,
    EVENT_SET_SCENE_SUCCESS,
    EVENT_SET_SCENE_FAILED,
    EVENT_SET_TRIGGER_SUCCESS,
    EVENT_SET_TRIGGER_FAILED,
    EVENT_RESET_NODE_SUCCESS
  ];
}


RCT_EXPORT_MODULE(TelinkBle)

- (void) setOnOff:(int)address onOff:(int)onOff
{
  [DemoCommand switchOnOffWithIsOn:onOff address:address responseMaxCount:(int)1 ack:YES successCallback:^(UInt16 source, UInt16 destination, SigGenericOnOffStatus * _Nonnull responseMessage) {
    // Callback
  } resultCallback:^(BOOL isResponseAll, NSError * _Nonnull error) {
    // Result callback
  }];
}

RCT_EXPORT_METHOD(setOnOff:(nonnull NSNumber *)address withOnOffStatus:(nonnull NSNumber*)onOff)
{
  [self setOnOff:[address intValue] onOff:[onOff intValue]];
}

RCT_EXPORT_METHOD(setAllOn)
{
  [self setOnOff:kMeshAddress_allNodes onOff:1];
}

RCT_EXPORT_METHOD(setAllOff)
{
  [self setOnOff:kMeshAddress_allNodes onOff:0];
}

RCT_EXPORT_METHOD(setHsl:(nonnull NSNumber*)address hsl:(nonnull NSDictionary*)hsl)
{
  
}

RCT_EXPORT_METHOD(setLuminance:(nonnull NSNumber*)address luminance:(nonnull NSNumber*)luminance)
{
  UInt16 lightness = [SigHelper.share getUint16LightnessFromUInt8Lum:[luminance intValue]];
  [SDKLibCommand
   lightLightnessSetWithDestination:[address intValue]
   lightness:lightness
   retryCount:1
   responseMaxCount:1
   ack:true
   successCallback:^(UInt16 source, UInt16 destination, SigLightLightnessStatus * _Nonnull responseMessage) {
    //
  } resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
    //
  }];
}

RCT_EXPORT_METHOD(setTemp:(nonnull NSNumber*)address withTemp:(nonnull NSNumber*)temp)
{
  UInt16 temperature = [SigHelper.share getUint16TemperatureFromUInt8Temperature100:[temp intValue]];
  [SDKLibCommand
   lightCTLTemperatureSetWithDestination:[address intValue]
   temperature:temperature
   deltaUV:0
   retryCount:1
   responseMaxCount:1
   ack:true
   successCallback:^(UInt16 source, UInt16 destination, SigLightCTLTemperatureStatus * _Nonnull responseMessage) {
    //
  } resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
    //
  }];
}

RCT_EXPORT_METHOD(autoConnect)
{
  [SigBearer.share startMeshConnectWithComplete:nil];
}

RCT_EXPORT_METHOD(stopScanning)
{
  [SDKLibCommand stopScan];
}

RCT_EXPORT_METHOD(startScanning)
{
  TeLogVerbose(@"");
  NSData *key = [SigDataSource.share curNetKey];
  UInt16 provisionAddress = [SigDataSource.share provisionAddress];
  
  if (provisionAddress == 0) {
    TeLogDebug(@"warning: address has run out.");
    return;
  }
  
  __weak typeof(self) weakSelf = self;
  NSNumber *type = [[NSUserDefaults standardUserDefaults] valueForKey:KeyBindType];
  [SigBearer.share stopMeshConnectWithComplete:^(BOOL successful) {
    TeLogVerbose(@"successful=%d",successful);
    if (successful) {
      TeLogInfo(@"stop mesh success.");
      __block UInt16 currentProvisionAddress = provisionAddress;
      __block NSString *currentAddUUID = nil;
      [SDKLibCommand
       startAddDeviceWithNextAddress:provisionAddress
       networkKey:key
       netkeyIndex:SigDataSource.share.curNetkeyModel.index
       appkeyModel:SigDataSource.share.curAppkeyModel
       unicastAddress:0
       uuid:nil
       keyBindType:type.integerValue
       productID:0
       cpsData:nil
       isAutoAddNextDevice:YES
       /**
        * Handle provisioning success
        */
       provisionSuccess:^(NSString * _Nonnull identify, UInt16 address) {
        if (identify && address != 0) {
          currentAddUUID = identify;
          currentProvisionAddress = address;
          [weakSelf updateDeviceProvisionSuccess:identify address:address];
          TeLogInfo(@"addDevice_provision success : %@->0X%X",identify,address);
        }
      }
       /**
        * Handle provisioning failure
        */
       provisionFail:^(NSError * _Nonnull error) {
        [weakSelf updateDeviceProvisionFail:SigBearer.share.getCurrentPeripheral.identifier.UUIDString];
        TeLogInfo(@"addDevice provision fail error:%@",error);
      }
       /**
        * Handle key binding success
        */
       keyBindSuccess:^(NSString * _Nonnull identify, UInt16 address) {
        if (identify && address != 0) {
          currentProvisionAddress = address;
          [weakSelf updateDeviceKeyBind:currentAddUUID address:currentProvisionAddress isSuccess:YES];
          TeLogInfo(@"addDevice_provision success : %@->0X%X",identify,address);
        }
      }
       /**
        * Handle key binding failure
        */
       keyBindFail:^(NSError * _Nonnull error) {
        [weakSelf updateDeviceKeyBind:currentAddUUID address:currentProvisionAddress isSuccess:NO];
        TeLogInfo(@"addDevice keybind fail error:%@",error);
      }
       finish:^{
        TeLogInfo(@"addDevice finish.");
        [SigBearer.share startMeshConnectWithComplete:nil];
        dispatch_async(dispatch_get_main_queue(), ^{
          [weakSelf addDeviceFinish];
        });
      }];
    } else {
      TeLogInfo(@"stop mesh fail.");
    }
  }];
}

- (void)updateDeviceProvisionSuccess:(NSString *)uuid address:(UInt16)address {
  SigScanRspModel *scanModel = [SigDataSource.share getScanRspModelWithUUID:uuid];
  AddDeviceModel *model = [[AddDeviceModel alloc] init];
  if (scanModel == nil) {
    scanModel = [[SigScanRspModel alloc] init];
    scanModel.uuid = uuid;
  }
  model.scanRspModel = scanModel;
  model.scanRspModel.address = address;
  model.state = AddDeviceModelStateBinding;
  if (![self.source containsObject:model]) {
    [self.source addObject:model];
  }
  dispatch_async(dispatch_get_main_queue(), ^{
    // TODO: handle provisioning success
    NSNumber* addr = [NSNumber numberWithUnsignedShort:[scanModel address]];
    [self sendEventWithName:EVENT_PROVISIONING_SUCCESS body:@{
      @"address": addr,
      @"macAddress": [scanModel macAddress],
      @"uuid": [scanModel uuid],
    }];
  });
}

- (void)updateDeviceProvisionFail:(NSString *)uuid {
  SigScanRspModel *scanModel = [SigDataSource.share getScanRspModelWithUUID:uuid];
  AddDeviceModel *model = [[AddDeviceModel alloc] init];
  if (scanModel == nil) {
    scanModel = [[SigScanRspModel alloc] init];
    scanModel.uuid = uuid;
  }
  model.scanRspModel = scanModel;
  //    model.scanRspModel.address = address;
  model.state = AddDeviceModelStateProvisionFail;
  if (![self.source containsObject:model]) {
    [self.source addObject:model];
  }
  dispatch_async(dispatch_get_main_queue(), ^{
    // TODO: handle provisioning failure
    ProvisioningDevice* provisioningDevice = [[ProvisioningDevice alloc] init];
    provisioningDevice.uuid = scanModel.uuid;
    [self sendEventWithName:EVENT_PROVISIONING_FAILED body:provisioningDevice];
    [provisioningDevice release];
  });
}

- (void)updateDeviceKeyBind:(NSString *)uuid address:(UInt16)address isSuccess:(BOOL)isSuccess{
  NSArray *source = [NSArray arrayWithArray:self.source];
  for (AddDeviceModel *model in source) {
    if ([model.scanRspModel.uuid isEqualToString:uuid] || model.scanRspModel.address == address) {
      if (isSuccess) {
        model.state = AddDeviceModelStateBindSuccess;
        dispatch_async(dispatch_get_main_queue(), ^{
          // TODO: handle binding success
          [self sendEventWithName:EVENT_BINDING_SUCCESS body:model];
        });
      } else {
        model.state = AddDeviceModelStateBindFail;
        dispatch_async(dispatch_get_main_queue(), ^{
          // TODO: handle binding failure
          [self sendEventWithName:EVENT_BINDING_FAILED body:model];
        });
      }
      break;
    }
  }
}

- (void) addDeviceFinish {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self sendEventWithName:EVENT_SCANNING_TIMEOUT body:nil];
  });
}

RCT_EXPORT_METHOD(getNodes:(RCTPromiseResolveBlock)resolve
                  withRejecter:(RCTPromiseRejectBlock)reject)
{
  NSMutableArray<SigNodeModel*> *source = [NSMutableArray arrayWithArray:SigDataSource.share.curNodes];
  NSMutableArray<NSDictionary*>* arr = [[NSMutableArray alloc] init];
  for (SigNodeModel *model in source) {
    [arr addObject:@{
      @"unicastId": [NSNumber numberWithUnsignedShort:[model address]],
      @"uuid": [model UUID],
      @"deviceKey": [model deviceKey],
      @"isKeyBindSuccess": [NSNumber numberWithBool:[model isKeyBindSuccess]],
      @"address": [model macAddress],
      @"cid": [model cid],
      @"pid": [model pid],
      @"defaultTTL": [NSNumber numberWithInteger:[model defaultTTL]],
      @"crpl": [model crpl],
      @"security": [model security],
    }];
  }
  resolve(arr);
}

@end
