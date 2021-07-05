/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#import "AppDelegate.h"

#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>
#import <TelinkSigMeshLib/TelinkSigMeshLib.h>

#if RCT_DEV
#import <React/RCTDevLoadingView.h>
#endif

#ifdef FB_SONARKIT_ENABLED
#import <FlipperKit/FlipperClient.h>
#import <FlipperKitLayoutPlugin/FlipperKitLayoutPlugin.h>
#import <FlipperKitUserDefaultsPlugin/FKUserDefaultsPlugin.h>
#import <FlipperKitNetworkPlugin/FlipperKitNetworkPlugin.h>
#import <SKIOSNetworkPlugin/SKIOSNetworkAdapter.h>
#import <FlipperKitReactPlugin/FlipperKitReactPlugin.h>
static void InitializeFlipper(UIApplication *application) {
  FlipperClient *client = [FlipperClient sharedClient];
  SKDescriptorMapper *layoutDescriptorMapper = [[SKDescriptorMapper alloc] initWithDefaults];
  [client addPlugin:[[FlipperKitLayoutPlugin alloc] initWithRootNode:application withDescriptorMapper:layoutDescriptorMapper]];
  [client addPlugin:[[FKUserDefaultsPlugin alloc] initWithSuiteName:nil]];
  [client addPlugin:[FlipperKitReactPlugin new]];
  [client addPlugin:[[FlipperKitNetworkPlugin alloc] initWithNetworkAdapter:[SKIOSNetworkAdapter new]]];
  [client start];
}
#endif

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
#ifdef FB_SONARKIT_ENABLED
  InitializeFlipper(application);
#endif
  RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:launchOptions];
  
#if RCT_DEV
  [bridge moduleForClass:[RCTDevLoadingView class]];
#endif
  RCTRootView *rootView = [[RCTRootView alloc] initWithBridge:bridge
                                                   moduleName:@"TelinkBleExample"
                                            initialProperties:nil];
  
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];
  
  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  
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
  
  //demo中setting界面显示的log信息，客户开发到后期，APP稳定后可以不集成该功能，且上架最好关闭log保存功能。(客户发送iTunes中的日志文件“TelinkSDKDebugLogData”给泰凌微即可)
  [SigLogger.share setSDKLogLevel:SigLogLevelDebug];
  //    [SigLogger.share setSDKLogLevel:SigLogLevelAll];
  
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
  //    [SigDataSource.share.defaultGroupSubscriptionModels addObject:@(0x00000211)];//新增vendorModelID用于测试加组及vendor组控。
  
  //(可选)SDK默认实现了PID为1和7的设备的fast bind功能，其它类型的设备可通过以下接口添加该类型设备默认的nodeInfo以实现fast bind功能
  //    DeviceTypeModel *model = [[DeviceTypeModel alloc] initWithCID:kCompanyID PID:8];
  //    NSData *nodeInfoData = [NSData dataWithBytes:TemByte length:76];
  //    [model setDefultNodeInfoData:nodeInfoData];
  //    [SigDataSource.share.defaultNodeInfos addObject:model];
  
  //(可选)SDK默认publish周期为20秒，通过修改可以修改SDK的默认publish参数，或者客户自行实现publish检测机制。
  //    SigPeriodModel *periodModel = [[SigPeriodModel alloc] init];
  //    periodModel.numberOfSteps = kPublishIntervalOfDemo;
  ////    periodModel.numberOfSteps = 5;//整形，范围0x01~0x3F.
  //    periodModel.resolution = [LibTools getSigStepResolutionInMillisecondsOfJson:SigStepResolution_seconds];
  //    SigDataSource.share.defaultPublishPeriodModel = periodModel;
  
  
  SigMeshLib.share.transmissionTimerInteral = 0.600;
  
  //    SigDataSource.share.needPublishTimeModel = NO;
  
#if DEBUG
  [SigLogger.share setSDKLogLevel:SigLogLevelDebug];
#else
  [SigLogger.share setSDKLogLevel:SigLogLevelWarning];
#endif
  
  return YES;
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

@end
