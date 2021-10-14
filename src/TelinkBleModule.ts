import type { NodeInfo } from 'react-native-telink-ble';

export abstract class TelinkBleModule {
  /**
   * Start mesh network connection
   */
  public abstract autoConnect(): void;

  /**
   * Get provisioned node list
   */
  public abstract getNodes(): Promise<NodeInfo[]>;

  /**
   * Send raw command in hex string to BLE network
   *
   * @param command {string} - Raw BLE command
   */
  public abstract sendRawString(command: string): void;

  /**
   * Start unprovisioned device scanning
   */
  public abstract startScanning(): void;

  /**
   * Stop unprovisioned device scanning
   */
  public abstract stopScanning(): void;

  /**
   * Start adding all unprovisioned devices and bind them with mesh application key
   */
  public abstract startAddingAllDevices(): void;

  /**
   *
   * @param meshAddress {number} - Node address
   * @param status {boolean} - On-off status
   */
  public abstract setStatus(meshAddress: number, status: boolean): void;

  /**
   *
   * @param meshAddress {number} - Node address
   * @param brightness {number} - Brightness (0..100)
   */
  public abstract setBrightness(meshAddress: number, brightness: number): void;

  /**
   *
   * @param meshAddress {number} - Node address
   * @param temperature {number} - Light temperature (0..100)
   */
  public abstract setTemperature(
    meshAddress: number,
    temperature: number
  ): void;

  /**
   * Set HSL for RGB lamps
   *
   * @param meshAddress {number} - Node address
   * @param hsl {{
       h: number;
       s: number;
       l: number;
     }} - HSL object (h: 0..360, s: 0..100, l: 0..100)
   */
  public abstract setHSL(
    meshAddress: number,
    hsl: {
      h: number;
      s: number;
      l: number;
    }
  ): void;

  /**
   * Share mesh data as QR code
   *
   * @param path {string}
   */
  public abstract shareQRCode(path: string): Promise<string>;

  /**
   * Reset BLE node
   *
   * @param meshAddress {number} - Node address
   */
  public abstract resetNode(meshAddress: number): void;

  /**
   * Set message delegate for native module on iOS
   *
   * (iOS Only)
   *
   * @returns {void}
   */
  public abstract setDelegateForIOS(): void;
}
