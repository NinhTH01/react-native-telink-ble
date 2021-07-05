//
//  ProvisioningDevice.h
//  TelinkBleExample
//
//  Created by Thanh Tùng on 05/07/2021.
//

#ifndef ProvisioningDevice_h
#define ProvisioningDevice_h

#import <Foundation/Foundation.h>

@interface ProvisioningDevice : NSDictionary

@property(nonatomic, strong) NSString* address;

@property(nonatomic, strong) NSNumber* meshAddress;

@property(nonatomic, strong) NSString* uuid;

@end

#endif /* ProvisioningDevice_h */
