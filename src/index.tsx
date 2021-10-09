import {
  EventSubscriptionVendor,
  NativeEventEmitter,
  NativeModules,
  Platform,
} from 'react-native';
import { RNTelinkBle } from './RNTelinkBle';

const TelinkBle: RNTelinkBle & EventSubscriptionVendor =
  NativeModules.TelinkBle;

Object.setPrototypeOf(TelinkBle, RNTelinkBle.prototype);
TelinkBle.eventEmitter = new NativeEventEmitter(TelinkBle);
if (Platform.OS === 'ios') {
  TelinkBle.setDelegateForIOS();
}

export default TelinkBle;

export type { NodeInfo } from './NodeInfo';
export type { UnprovisionedDevice } from './UnprovisionedDevice';
export { RNTelinkBle };
export * from './helpers/native';
export type { MeshStatus } from './MeshStatus';
