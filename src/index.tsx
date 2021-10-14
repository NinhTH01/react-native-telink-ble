import {
  EventSubscriptionVendor,
  NativeEventEmitter,
  NativeModules,
  Platform,
} from 'react-native';
import { RNTelinkBle } from './RNTelinkBle';

const TelinkBle: RNTelinkBle & EventSubscriptionVendor =
  NativeModules.TelinkBle;

TelinkBle.eventEmitter =
  Platform.OS === 'ios'
    ? new NativeEventEmitter(TelinkBle)
    : new NativeEventEmitter();

Object.setPrototypeOf(TelinkBle, RNTelinkBle.prototype);

if (Platform.OS === 'ios') {
  TelinkBle.setDelegateForIOS();
}

export default TelinkBle;

export type { NodeInfo } from './NodeInfo';
export type { DeviceInfo } from './DeviceInfo';
export { RNTelinkBle };
export * from './helpers/native';
export type { MeshStatus } from './MeshStatus';
