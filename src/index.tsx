import { NativeModules } from 'react-native';
import { NATIVE_MODULE_NAME } from './config';

interface TelinkBleType {
  multiply(a: number, b: number): Promise<number>;
}

const TelinkBle: TelinkBleType = NativeModules[NATIVE_MODULE_NAME];

export default TelinkBle;
