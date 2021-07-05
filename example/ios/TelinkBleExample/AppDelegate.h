/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#import <React/RCTBridgeDelegate.h>
#import <UIKit/UIKit.h>

//分享使用方式(是否使用蓝牙点对点传输，YES为二维码加蓝牙点对点，NO为存二维码)
#define kShareWithBluetoothPointToPoint (YES)
//setting界面显示
#define kShowScenes (YES)
#define kShowDebug  (NO)
#define kshowLog        (YES)
#define kshowShare    (YES)
#define kshowMeshInfo    (YES)
#define kshowChooseAdd    (YES)

#define kKeyBindType                      @"kKeyBindType"
#define kRemoteAddType                    @"kRemoteAddType"
#define kFastAddType                      @"kFastAddType"
#define kDLEType                          @"kDLEType"
#define kGetOnlineStatusType              @"kGetOnlineStatusType"
#define kAddStaticOOBDevcieByNoOOBEnable  @"kAddStaticOOBDevcieByNoOOBEnable"

#define kDLEUnsegmentLength (229)

@interface AppDelegate : UIResponder <UIApplicationDelegate, RCTBridgeDelegate>

@property (nonatomic, strong) UIWindow *window;

@end
