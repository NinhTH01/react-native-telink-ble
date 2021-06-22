import { NativeEventEmitter, NativeModules } from 'react-native';
import { NATIVE_MODULE_NAME } from './config';
import { BleEvent } from './ble-event';
import type { Device } from './device';
import type { TelinkBleNativeModule } from './telink-ble-native-module';

const TelinkBleModule: TelinkBleNativeModule =
  NativeModules[NATIVE_MODULE_NAME];

class TelinkBle implements TelinkBleNativeModule {
  private eventEmitter: NativeEventEmitter;

  public constructor() {
    this.eventEmitter = new NativeEventEmitter();
  }

  public startScanning() {
    return TelinkBleModule.startScanning();
  }

  public stopScanning() {
    return TelinkBleModule.stopScanning();
  }

  public createMeshNetwork(): Promise<string> {
    return TelinkBleModule.createMeshNetwork();
  }

  public initMeshNetwork(networkKey: string): Promise<string> {
    return TelinkBleModule.initMeshNetwork(networkKey);
  }

  /**
   * Add BLE event listener
   * @param event {BleEvent} - BLE event
   * @param listener {(data: any) => void | Promise<void>} - callback function
   *
   * @return {() => void} - the unsubscribe function
   */
  public addEventListener(
    event: BleEvent,
    listener: (data: any) => void | Promise<void>
  ) {
    this.eventEmitter.addListener(event, listener);

    return () => {
      this.eventEmitter.removeListener(event, listener);
    };
  }

  /**
   * On device found handler
   *
   * @param listener {(device: Device) => void | Promise<void>} - device found handler function
   * @return {() => void} - function to unsubscribe
   */
  public addDeviceFoundListener(
    listener: (device: Device) => void | Promise<void>
  ): () => void {
    return this.addEventListener(BleEvent.EVENT_DEVICE_FOUND, listener);
  }

  /**
   * Scanning timeout handler
   *
   * @param listener {() => void | Promise<void>} - scanning timeout handler function
   * @return {() => void} - function to unsubscribe
   */
  public addScanningTimeoutListener(listener: () => void | Promise<void>) {
    return this.addEventListener(BleEvent.EVENT_SCANNING_TIMEOUT, listener);
  }
}

export default new TelinkBle();
export type { Device };
export { BleEvent };
export { BondState } from './bond-state';
