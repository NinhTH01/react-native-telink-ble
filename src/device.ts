import type { BondState } from './bond-state';

/**
 * Ble device type
 */
export interface Device {
  /**
   * Device name string
   *
   * @type {string | null}
   */
  name?: string | null;

  /**
   * Device MAC address
   *
   * @type {string}
   */
  address: string;

  /**
   * Represents a scan record from Bluetooth LE scan
   *
   * @type {string} - Hex string
   */
  scanRecord: string;

  /**
   * Received Signal Strength Indication
   *
   * @type {number}
   */
  rssi: number;

  /**
   * Device UUID
   *
   * @type {string} - Hex string
   */
  uuid: string;

  /**
   * Device type
   *
   * @type {number}
   */
  type: number;

  /**
   * Device bond state
   *
   * @type {BondState}
   */
  bondState: BondState;

  bluetoothClass?: string;

  deviceClass?: string;

  majorDeviceClass?: string;
}
