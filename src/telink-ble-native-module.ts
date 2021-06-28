import type { BluetoothState } from './bluetooth-state';
import type { HSL } from 'colorsys';

export interface TelinkBleNativeModule {
  /**
   * Start device scanning
   */
  startScanning(): void;

  /**
   * Stop device scanning
   */
  stopScanning(): void;

  /**
   * Initialize mesh network with existing key
   */
  initMeshService(): void;

  /**
   * Check bluetooth state
   *
   * @return {Promise<BluetoothState>}
   */
  checkBluetoothPermission(): Promise<BluetoothState>;

  /**
   * Start provisioning a device
   *
   * @param deviceUUID {string} - device UUID in hex string
   * @return {Promise<number>} - Unicast ID of device in mesh network
   */
  startProvisioning(deviceUUID: string): Promise<number>;

  getNodes(): Promise<any[]>;

  autoConnect(): void;

  setOnOff(address: number, onOff: number): void;

  setAllOn(): void;

  setAllOff(): void;

  setLuminance(address: number, luminance: number): void;

  setTemp(address: number, temp: number): void;

  setHsl(address: number, hsl: HSL): void;
}
