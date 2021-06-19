import { NativeModules } from 'react-native';
import { NATIVE_MODULE_NAME } from './config';

interface TelinkBleType {
  startScanning(): void;

  stopScanning(): void;

  initMeshNetwork(networkKey: string): Promise<string>;

  createMeshNetwork(): Promise<string>;
}

const TelinkBle: TelinkBleType = NativeModules[NATIVE_MODULE_NAME];

export default TelinkBle;
