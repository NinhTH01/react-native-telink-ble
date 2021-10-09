//
//  TelinkBle+DeviceScanning.m
//  TelinkBle
//
//  Created by Thanh Tùng on 09/10/2021.
//  Copyright © 2021 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TelinkBle+DeviceScanning.h"

@implementation TelinkBle (DeviceScanning)

- (void)startAddingAllDevices
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
             provisionSuccess:^(NSString * _Nonnull identify, UInt16 address) {
                if (identify && address != 0) {
                    currentAddUUID = identify;
                    currentProvisionAddress = address;
                    [weakSelf updateDeviceProvisionSuccess:identify address:address];
                    TeLogInfo(@"addDevice_provision success : %@->0X%X",identify,address);
                }
            }
             provisionFail:^(NSError * _Nonnull error) {
                [weakSelf updateDeviceProvisionFail:SigBearer.share.getCurrentPeripheral.identifier.UUIDString];
                TeLogInfo(@"addDevice provision fail error:%@",error);
            }
             keyBindSuccess:^(NSString * _Nonnull identify, UInt16 address) {
                if (identify && address != 0) {
                    currentProvisionAddress = address;
                    [weakSelf updateDeviceKeyBind:currentAddUUID address:currentProvisionAddress isSuccess:YES];
                    TeLogInfo(@"addDevice_provision success : %@->0X%X",identify,address);
                }
            }
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

- (void)updateDeviceProvisionSuccess:(NSString *)uuid address:(UInt16)address
{
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
        [self sendEventWithName:EVENT_UNPROVISIONED_DEVICE_FOUND body:[self getJSModel:scanModel]];
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
        [self sendEventWithName:EVENT_PROVISIONING_FAILED body:[self getJSModel:scanModel]];
    });
}

- (void)updateDeviceKeyBind:(NSString *)uuid address:(UInt16)address isSuccess:(BOOL)isSuccess{
    NSArray *source = [NSArray arrayWithArray:self.source];
    for (AddDeviceModel *model in source) {
        if ([model.scanRspModel.uuid isEqualToString:uuid] || model.scanRspModel.address == address) {
            SigScanRspModel* scanModel = model.scanRspModel;
            if (isSuccess) {
                model.state = AddDeviceModelStateBindSuccess;
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self sendEventWithName:EVENT_BINDING_SUCCESS body:[self getJSModel:scanModel]];
                });
            } else {
                model.state = AddDeviceModelStateBindFail;
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self sendEventWithName:EVENT_BINDING_FAILED body:[self getJSModel:scanModel]];
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

@end
