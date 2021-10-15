import {
  EventSubscriptionVendor,
  NativeEventEmitter,
  NativeModules,
  Platform,
} from 'react-native';
import { RNTelinkBle } from './RNTelinkBle';

const TelinkBle: RNTelinkBle & EventSubscriptionVendor =
  NativeModules.TelinkBle;

TelinkBle.eventEmitter = new NativeEventEmitter(TelinkBle);

Object.setPrototypeOf(TelinkBle, RNTelinkBle.prototype);

if (Platform.OS === 'ios') {
  TelinkBle.setDelegateForIOS();
}

export default TelinkBle;
export { RNTelinkBle };
export type { NodeInfo } from './NodeInfo';
export type { DeviceInfo } from './DeviceInfo';
export type { MeshStatus } from './MeshStatus';
export type { HSL } from './HSL';
export * from './helpers/native';
