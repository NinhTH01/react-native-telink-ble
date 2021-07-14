//
//  TelinkBle.m
//  TelinkBleExample
//
//  Created by Thanh Tùng on 02/07/2021.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
#import "TelinkBle.h"
#import "AddDeviceStateModel.h"
#import "DemoCommand.h"

@implementation TelinkBle

RCT_EXPORT_MODULE()

RCT_EXTERN_METHOD(startMeshSDK)

RCT_EXTERN_METHOD(getNodes:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(startScanning)

RCT_EXTERN_METHOD(stopScanning)

RCT_EXTERN_METHOD(resetBle)

RCT_EXTERN_METHOD(kickOut:(nonnull NSNumber*)address)

RCT_EXTERN_METHOD(forceRemoveNodeAtAddress:(nonnull NSNumber*)address)

RCT_EXTERN_METHOD(setOnOff:(int)address onOff:(int)onOff)

RCT_EXTERN_METHOD(setAllOn)

RCT_EXTERN_METHOD(setAllOff)

RCT_EXTERN_METHOD(setLuminance:(nonnull NSNumber*)address withLuminance:(nonnull NSNumber*)luminance)

RCT_EXTERN_METHOD(setHsl:(nonnull NSNumber*)address withHSL:(nonnull NSDictionary*)hsl)

RCT_EXTERN_METHOD(setTemp:(nonnull NSNumber*)address withTemp:(nonnull NSNumber*)temperature)

RCT_EXTERN_METHOD(autoConnect)

RCT_EXTERN_METHOD(removeSceneFromDevice:(nonnull NSNumber*)sceneId withDeviceId:(nonnull NSNumber*)deviceId)

RCT_EXTERN_METHOD(triggerScene:(nonnull NSNumber*)sceneId withRetryCount:(nonnull NSNumber*)retryCount)

RCT_EXTERN_METHOD(addDeviceToGroup:(nonnull NSNumber*)groupId withDeviceId:(nonnull NSNumber*)deviceId)

RCT_EXTERN_METHOD(removeDeviceFromGroup:(nonnull NSNumber*)groupId withDeviceId:(nonnull NSNumber*)deviceId)

#pragma mark - class startMeshSDK
+ (void)startMeshSDK
{
    //demo v2.8.0新增快速添加模式，demo默认使用普通添加模式。
    NSNumber *type = [[NSUserDefaults standardUserDefaults] valueForKey:kKeyBindType];
    if (type == nil) {
        type = [NSNumber numberWithInteger:KeyBindTpye_Normal];
        [[NSUserDefaults standardUserDefaults] setValue:type forKey:kKeyBindType];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    //demo v2.8.0新增remote添加模式，demo默认使用普通添加模式。
    NSNumber *remoteType = [[NSUserDefaults standardUserDefaults] valueForKey:kRemoteAddType];
    if (remoteType == nil) {
        remoteType = [NSNumber numberWithBool:NO];
        [[NSUserDefaults standardUserDefaults] setValue:remoteType forKey:kRemoteAddType];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    //demo v2.8.1新增私有定制getOnlinestatus，demo默认使用私有定制获取状态。
    NSNumber *onlineType = [[NSUserDefaults standardUserDefaults] valueForKey:kGetOnlineStatusType];
    if (onlineType == nil) {
        onlineType = [NSNumber numberWithBool:YES];
        [[NSUserDefaults standardUserDefaults] setValue:onlineType forKey:kGetOnlineStatusType];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    //demo v3.0.0新增fast provision添加模式，demo默认使用普通添加模式。
    NSNumber *fastAddType = [[NSUserDefaults standardUserDefaults] valueForKey:kFastAddType];
    if (fastAddType == nil) {
        fastAddType = [NSNumber numberWithBool:NO];
        [[NSUserDefaults standardUserDefaults] setValue:fastAddType forKey:kFastAddType];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    //demo v3.2.2新增staticOOB设备添加的兼容模式，demo默认使用兼容模式。（兼容模式为staticOOB设备在无OOB数据的情况下通过noOOB provision的方式进行添加;不兼容模式为staticOOB设备必须通过staticOOB provision的方式进行添加）。
    NSNumber *addStaticOOBDevcieByNoOOBEnable = [[NSUserDefaults standardUserDefaults] valueForKey:kAddStaticOOBDevcieByNoOOBEnable];
    if (addStaticOOBDevcieByNoOOBEnable == nil) {
        addStaticOOBDevcieByNoOOBEnable = [NSNumber numberWithBool:YES];
        [[NSUserDefaults standardUserDefaults] setValue:addStaticOOBDevcieByNoOOBEnable forKey:kAddStaticOOBDevcieByNoOOBEnable];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    SigDataSource.share.addStaticOOBDevcieByNoOOBEnable = addStaticOOBDevcieByNoOOBEnable.boolValue;
    //demo v3.3.0新增DLE模式，demo默认关闭DLE模式。（客户定制功能）
    NSNumber *DLEType = [[NSUserDefaults standardUserDefaults] valueForKey:kDLEType];
    if (DLEType == nil) {
        DLEType = [NSNumber numberWithBool:NO];
        [[NSUserDefaults standardUserDefaults] setValue:DLEType forKey:kDLEType];
        [[NSUserDefaults standardUserDefaults] synchronize];
    } else {
        if (DLEType.boolValue) {
            //(可选)打开DLE功能后，SDK的Access消息是长度大于229字节才进行segment分包。
            SigDataSource.share.defaultUnsegmentedMessageLowerTransportPDUMaxLength = kDLEUnsegmentLength;
        }
    }
    /*初始化SDK*/
    //1.一个provisioner分配的地址范围，默认为0x400。
    SigDataSource.share.defaultAllocatedUnicastRangeHighAddress = kAllocatedUnicastRangeHighAddress;
    //2.mesh网络的sequenceNumber步长，默认为128。
    //    SigDataSource.share.defaultSnoIncrement = kSnoIncrement;
    SigDataSource.share.defaultSnoIncrement = 16;
    //3.启动SDK。
    [SDKLibCommand startMeshSDK];
    //(可选)SDK的分组默认绑定5个modelID，可通过以下接口修改分组默认绑定的modelIDs
    SigDataSource.share.defaultGroupSubscriptionModels = [NSMutableArray arrayWithArray:@[@(kSigModel_GenericOnOffServer_ID),@(kSigModel_LightLightnessServer_ID),@(kSigModel_LightCTLServer_ID),@(kSigModel_LightCTLTemperatureServer_ID),@(kSigModel_LightHSLServer_ID)]];
    
    SigMeshLib.share.transmissionTimerInteral = 0.600;
    // SigDataSource.share.needPublishTimeModel = NO;
#if DEBUG
    [SigLogger.share setSDKLogLevel:SigLogLevelDebug];
#else
    [SigLogger.share setSDKLogLevel:SigLogLevelWarning];
#endif
}

#pragma mark - SigMessageDelegate
- (void)didReceiveMessage:(SigMeshMessage *)message sentFromSource:(UInt16)source toDestination:(UInt16)destination {
    if ([message isKindOfClass:[SigGenericOnOffStatus class]]) {
        SigGenericOnOffStatus* msg = (SigGenericOnOffStatus*) message;
        [self sendEventWithName:EVENT_DEVICE_ON_OFF_STATUS body:@{
            @"address": [NSNumber numberWithUnsignedShort:source],
            @"isOn": [NSNumber numberWithBool:!msg.isOn],
        }];
    }
    
    if ([message isKindOfClass:[SigTelinkOnlineStatusMessage class]]) {
        
    }
    
    if ([message isKindOfClass:[SigLightLightnessStatus class]]) {
        [self sendEventWithName:EVENT_DEVICE_LIGHTNESS body:@{
            @"address": [NSNumber numberWithUnsignedShort:source],
            @"lightness": [NSNumber numberWithUnsignedShort:[SigHelper.share getUInt8LumFromUint16Lightness:[((SigLightLightnessStatus*)message) targetLightness]]],
        }];
    }
    
    if ([message isKindOfClass:[SigLightLightnessLastStatus class]]) {
        [self sendEventWithName:EVENT_DEVICE_LIGHTNESS body:@{
            @"address": [NSNumber numberWithUnsignedShort:source],
            @"lightness": [NSNumber numberWithUnsignedShort:[SigHelper.share getUInt8LumFromUint16Lightness:[((SigLightLightnessLastStatus*)message) lightness]]],
        }];
    }
    
    if ([message isKindOfClass:[SigLightCTLStatus class]]) {
        [self sendEventWithName:EVENT_DEVICE_CTL body:@{
            @"address": [NSNumber numberWithUnsignedShort:source],
            @"temperature": [NSNumber numberWithUnsignedShort:[SigHelper.share getUInt8Temperature100FromUint16Temperature:[((SigLightCTLStatus*)message) targetCTLTemperature]]],
        }];
    }
    
    if ([message isKindOfClass:[SigLightHSLStatus class]]) {
        SigLightHSLStatus* msg = (SigLightHSLStatus*)message;
        UInt8 l = [SigHelper.share getUInt8LumFromUint16Lightness:[msg HSLLightness]];
        UInt8 s = [SigHelper.share getUInt8LumFromUint16Lightness:[msg HSLSaturation]];
        UInt16 h = floor(((double) [msg HSLHue]) / 0xFFFF * 360);
        [self sendEventWithName:EVENT_DEVICE_HSL body:@{
            @"address": [NSNumber numberWithUnsignedShort:source],
            @"h": [NSNumber numberWithDouble:h],
            @"s": [NSNumber numberWithUnsignedInt:s],
            @"l": [NSNumber numberWithUnsignedInt:l],
        }];
    }
    
    if ([message isKindOfClass:[SigLightXyLStatus class]]) {
        
    }
    
    if ([message isKindOfClass:[SigLightLCLightOnOffStatus class]]) {
        
    }
}

#pragma mark - startMeshSDK
- (void)startMeshSDK
{
    [TelinkBle startMeshSDK];
}

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
        EVENT_RESET_NODE_SUCCESS,
        EVENT_RESET_NODE_FAILED,
        EVENT_MESH_CONNECT_SUCCESS,
        EVENT_MESH_CONNECT_FAILED,
        EVENT_DEVICE_RESPONSE,
        EVENT_BLE_SDK_BUSY,
        EVENT_REMOVE_SCENE_SUCCESS,
        EVENT_REMOVE_SCENE_FAIL,
        EVENT_DEVICE_ON_OFF_STATUS,
        EVENT_DEVICE_LIGHTNESS,
        EVENT_DEVICE_CTL,
        EVENT_DEVICE_HSL,
    ];
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
        [self sendEventWithName:EVENT_DEVICE_FOUND body:@{
            @"address": [NSNumber numberWithUnsignedShort:[scanModel address]],
            @"macAddress": [scanModel macAddress],
            @"uuid": [scanModel uuid],
            @"scanRecord": @"",
            @"provisioned": [NSNumber numberWithBool:[scanModel provisioned]],
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
    model.state = AddDeviceModelStateProvisionFail;
    if (![self.source containsObject:model]) {
        [self.source addObject:model];
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [self sendEventWithName:EVENT_PROVISIONING_FAILED body:scanModel.uuid];
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

- (void)addDeviceFinish {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self sendEventWithName:EVENT_SCANNING_TIMEOUT body:nil];
    });
}

#pragma mark - setOnOff
- (void)setOnOff:(int)address onOff:(int)onOff
{
    [DemoCommand switchOnOffWithIsOn:onOff address:address responseMaxCount:(int)1 ack:YES successCallback:^(UInt16 source, UInt16 destination, SigGenericOnOffStatus * _Nonnull responseMessage) {
        // Callback
    } resultCallback:^(BOOL isResponseAll, NSError * _Nonnull error) {
        // Result callback
    }];
}

- (void)setOnOff:(nonnull NSNumber *)address withOnOffStatus:(nonnull NSNumber*)onOff
{
    [self setOnOff:[address unsignedShortValue] onOff:[onOff intValue]];
}

#pragma mark - setAllOn
- (void)setAllOn
{
    [self setOnOff:kMeshAddress_allNodes onOff:1];
}

#pragma mark - setAllOff
- (void)setAllOff
{
    [self setOnOff:kMeshAddress_allNodes onOff:0];
}

#pragma mark - setLuminance
- (void)setLuminance:(nonnull NSNumber*)address withLuminance:(nonnull NSNumber*)luminance
{
    [DemoCommand
     changeBrightnessWithBrightness100:[luminance unsignedIntValue]
     address:[address unsignedShortValue]
     retryCount:SigDataSource.share.defaultRetryCount
     responseMaxCount:0
     ack:YES
     successCallback:^(UInt16 source, UInt16 destination, SigLightLightnessStatus * _Nonnull responseMessage) {
        //
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        if (isResponseAll) {
            //
        }
    }];
}

#pragma mark - setTemp
- (void)setTemp:(nonnull NSNumber*)address withTemp:(nonnull NSNumber*)temperature
{
    UInt8 tempValue = [temperature unsignedIntValue];
    [DemoCommand
     changeTempratureWithTemprature100:tempValue
     address:[address unsignedShortValue]
     retryCount:0
     responseMaxCount:0
     ack:NO
     successCallback:^(UInt16 source, UInt16 destination, SigLightCTLTemperatureStatus * _Nonnull responseMessage) {
        //
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        //
    }];
}

#pragma mark - setHsl
- (void)setHsl:(nonnull NSNumber*)address withHSL:(nonnull NSDictionary*)hsl
{
    double h = [[hsl valueForKey:@"h"] doubleValue] / 360 * 0xFFFF;
    double s = [[hsl valueForKey:@"s"] doubleValue] / 100 * 0xFFFF;
    double l = [[hsl valueForKey:@"l"] doubleValue] / 100 * 0xFFFF;
    [SDKLibCommand
     lightHSLSetWithDestination:[address unsignedShortValue]
     HSLLight:l
     HSLHue:h
     HSLSaturation:s
     retryCount:SigDataSource.share.defaultRetryCount
     responseMaxCount:1
     ack:YES
     successCallback:^(UInt16 source, UInt16 destination, SigLightHSLStatus * _Nonnull responseMessage) {
        //
    } resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        //
    }];
}

#pragma mark - autoConnect
- (void)autoConnect
{
    [SigBearer.share startMeshConnectWithComplete:^(BOOL successful) {
        NSString* eventName = (successful ? EVENT_MESH_CONNECT_SUCCESS : EVENT_MESH_CONNECT_FAILED);
        [self sendEventWithName:eventName body:nil];
    }];
}

#pragma mark - stopScanning
- (void)stopScanning
{
    [SDKLibCommand stopScan];
}

#pragma mark - startScanning
- (void)startScanning
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

#pragma mark - kickOut
- (void)getNodes:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject
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
            @"cidDesc": [model cid],
            @"pidDesc": [model pid],
            @"defaultTTL": [NSNumber numberWithInteger:[model defaultTTL]],
            @"crpl": [model crpl],
            @"security": [model security],
            @"lum": [NSNumber numberWithUnsignedShort:[model brightness]],
            @"temp": [NSNumber numberWithShort:[model temperature]],
            @"elementCnt": [NSNumber numberWithUnsignedInt:[model getElementCount]],
        }];
    }
    resolve(arr);
}

#pragma mark - kickOut
- (void)kickOut:(nonnull NSNumber*)address
{
    [DemoCommand
     kickoutDevice:[address unsignedShortValue]
     retryCount:SigDataSource.share.defaultRetryCount
     responseMaxCount:1
     successCallback:^(UInt16 source, UInt16 destination, SigConfigNodeResetStatus * _Nonnull responseMessage) {
        //
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        [SigDataSource.share deleteNodeFromMeshNetworkWithDeviceAddress:[address unsignedShortValue]];
        if (isResponseAll) {
            [self sendEventWithName:EVENT_RESET_NODE_SUCCESS body:address];
            return;
        }
        [self sendEventWithName:EVENT_RESET_NODE_FAILED body:address];
    }];
}

#pragma mark - addDeviceToGroup
- (void)addDeviceToGroup:(nonnull NSNumber*)groupId withDeviceId:(nonnull NSNumber*)deviceId;
{
    [DemoCommand
     editSubscribeListWithWithDestination:[deviceId unsignedShortValue]
     isAdd:YES
     groupAddress:[groupId unsignedShortValue]
     elementAddress:[deviceId unsignedShortValue]
     modelIdentifier:4096
     companyIdentifier:0
     retryCount:SigDataSource.share.defaultRetryCount
     responseMaxCount:1
     successCallback:^(UInt16 source, UInt16 destination, SigConfigModelSubscriptionStatus * _Nonnull responseMessage) {
        //
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        //
    }];
}

#pragma mark - removeDeviceFromGroup
- (void)removeDeviceFromGroup:(nonnull NSNumber*)groupId withDeviceId:(nonnull NSNumber*)deviceId;
{
    [DemoCommand
     editSubscribeListWithWithDestination:[deviceId unsignedShortValue]
     isAdd:NO
     groupAddress:[groupId unsignedShortValue]
     elementAddress:[deviceId unsignedShortValue]
     modelIdentifier:4096
     companyIdentifier:0
     retryCount:SigDataSource.share.defaultRetryCount
     responseMaxCount:1
     successCallback:^(UInt16 source, UInt16 destination, SigConfigModelSubscriptionStatus * _Nonnull responseMessage) {
        //
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        //
    }];
}

#pragma mark - setSceneForDevice
RCT_EXTERN_METHOD(setSceneForDevice:(nonnull NSNumber*)sceneId withDeviceId:(nonnull NSNumber*)deviceId)
- (void)setSceneForDevice:(nonnull NSNumber*)sceneId withDeviceId:(nonnull NSNumber*)deviceId
{
    [DemoCommand
     getSceneRegisterStatusWithAddress:[deviceId unsignedShortValue]
     responseMaxCount:1
     successCallback:^(UInt16 source, UInt16 destination, SigSceneRegisterStatus * _Nonnull responseMessage) {
        TeLogDebug(@"getSceneRegisterStatusWithAddress ResponseModel=%@",responseMessage.parameters);
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nonnull error) {
        if (error == nil) {
            [DemoCommand
             saveSceneWithAddress:[deviceId unsignedShortValue]
             sceneId:[sceneId unsignedShortValue]
             responseMaxCount:1
             ack:YES
             successCallback:^(UInt16 source, UInt16 destination, SigSceneRegisterStatus * _Nonnull responseMessage) {
                TeLogDebug(@"saveSceneWithAddress ResponseModel=%@",responseMessage.parameters);
            } resultCallback:^(BOOL isResponseAll, NSError * _Nonnull error) {
                if (error == nil) {
                    //
                }
            }];
        }
    }];
}

#pragma mark - removeSceneFromDevice
- (void)removeSceneFromDevice:(nonnull NSNumber*)sceneId withDeviceId:(nonnull NSNumber*)deviceId
{
    [DemoCommand
     delSceneWithAddress:[deviceId unsignedShortValue]
     sceneId:[sceneId unsignedShortValue]
     responseMaxCount:1
     ack:YES
     successCallback:^(UInt16 source, UInt16 destination, SigSceneRegisterStatus * _Nonnull responseMessage) {
        //
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nullable error) {
        //
    }];
}

#pragma mark - triggerScene
- (void)triggerScene:(nonnull NSNumber*)sceneId withRetryCount:(nonnull NSNumber*)retryCount
{
    [DemoCommand
     recallSceneWithAddress:0xFFFF
     sceneId:[sceneId unsignedShortValue]
     responseMaxCount:[retryCount intValue]
     ack:YES
     successCallback:^(UInt16 source, UInt16 destination, SigSceneStatus * _Nonnull responseMessage) {
        TeLogDebug(@"recall scene:%hu,status:%d",responseMessage.targetScene,responseMessage.statusCode);
    }
     resultCallback:^(BOOL isResponseAll, NSError * _Nonnull error) {
        
    }];
}

@end
