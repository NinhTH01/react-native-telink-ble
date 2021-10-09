import type { EventSubscription, NativeEventEmitter } from 'react-native';
import TelinkBle from 'react-native-telink-ble';
import { BleEvent } from './BleEvent';
import { uint16ToHexString } from './helpers/native';
import type { MeshStatus } from './MeshStatus';
import type { NodeInfo } from './NodeInfo';
import type { UnprovisionedDevice } from './UnprovisionedDevice';

export abstract class RNTelinkBle {
  /**
   * Native module event emitter
   *
   * @type {NativeEventEmitter}
   */
  public eventEmitter: NativeEventEmitter;

  /**
   * This class should be prototype only, not for creating new instance
   *
   * @param eventEmitter {NativeEventEmitter}
   */
  protected constructor(eventEmitter: NativeEventEmitter) {
    this.eventEmitter = eventEmitter;
  }

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
   * Turn on all devices
   */
  public setAllOn(): void {
    this.setStatus(0xffff, true);
  }

  /**
   * Turn off all devices
   */
  public setAllOff(): void {
    this.setStatus(0xffff, false);
  }

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
   * Set a scene for all devices
   *
   * @param sceneAddress {number} - Scene mesh address
   */
  public setScene(sceneAddress: number): void {
    TelinkBle.sendRawString(
      `a3 ff 00 00 00 00 02 00 ff ff 82 46 ${uint16ToHexString(sceneAddress)}`
    );
  }

  /**
   * Remove an existing scene
   *
   * @param sceneAddress {number} - Scene mesh address
   */
  public removeScene(sceneAddress: number): void {
    TelinkBle.sendRawString(
      `a3 ff 00 00 00 00 02 00 ff ff 82 9E ${uint16ToHexString(sceneAddress)}`
    );
  }

  /**
   * Recall a scene on mesh network
   *
   * @param sceneAddress {number} - Scene mesh address
   */
  public recallScene(sceneAddress: number): void {
    TelinkBle.sendRawString(
      `a3 ff 00 00 00 00 02 00 ff ff 82 42 ${uint16ToHexString(sceneAddress)}`
    );
  }

  /**
   * Get online state
   *
   * @return {void}
   */
  public abstract getOnlineState(): void;

  /**
   * Add a node to a group
   *
   * @param deviceAddress {number} - Node address
   * @param groupAddress {number} - Group address
   */
  public abstract addDeviceToGroup(
    deviceAddress: number,
    groupAddress: number
  ): void;

  /**
   * Remove a node from a group
   *
   * @param deviceAddress {number} - Node address
   * @param groupAddress {number} - Group address
   */
  public abstract removeDeviceFromGroup(
    deviceAddress: number,
    groupAddress: number
  ): void;

  public abstract shareQRCode(path: string): Promise<string>;

  /**
   * Reset BLE node
   *
   * @param meshAddress {number} - Node address
   */
  public abstract resetNode(meshAddress: number): void;

  /**
   * Node reset success event handler
   *
   * @param callback {() => void | Promise<void>} - Node reset callback
   * @returns {() => void}
   */
  public onNodeResetSuccess(callback: () => void | Promise<void>): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_NODE_RESET_SUCCESS,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  /**
   * Node reset failure event handler
   *
   * @param callback {() => void | Promise<void>} - Node reset callback
   * @returns {() => void}
   */
  public onNodeResetFailed(callback: () => void | Promise<void>): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_NODE_RESET_FAILED,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  /**
   * Setup handler for new unprovisioned device found event
   *
   * @param callback {(device: UnprovisionedDevice) => void | Promise<void>} - Unprovisioned device handler
   */
  public onUnprovisionedDeviceFound(
    callback: (device: UnprovisionedDevice) => void | Promise<void>
  ): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_UNPROVISIONED_DEVICE_FOUND,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  /**
   * Setup handler for scanning timeout event
   *
   * @param callback {() => void | Promise<void>} - Scanning timeout handler
   */
  public onScanningTimeout(callback: () => void | Promise<void>): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_SCANNING_TIMEOUT,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  /**
   * Setup handler for mesh network connection changes
   *
   * @param callback {(connected: boolean) => void | Promise<void>} - Mesh network handler
   */
  public onMeshConnected(
    callback: (connected: boolean) => void | Promise<void>
  ): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_MESH_NETWORK_CONNECTION,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  /**
   * Group success event handler
   *
   * @param callback
   * @returns
   */
  public onSetGroupSuccess(
    callback: (data: {
      groupAddress: number;
      deviceAddress: number;
      opcode: number;
    }) => void | Promise<void>
  ): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_SET_GROUP_SUCCESS,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  /**
   * Handle mesh response
   *
   * @param callback {(status: MeshStatus) => void | Promise<void>} - Mesh status response callback
   * @returns {() => void}
   */
  public onDeviceStatus(
    callback: (status: MeshStatus) => void | Promise<void>
  ): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_DEVICE_STATUS,
      callback
    );

    return () => {
      subscription.remove();
    };
  }

  public abstract setDelegateForIOS(): void;
}
