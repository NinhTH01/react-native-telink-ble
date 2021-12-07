import type { EventSubscription, NativeEventEmitter } from 'react-native';
import { BleEvent } from './BleEvent';
import type { DeviceInfo } from './DeviceInfo';
import { uint16ToHexString } from './helpers/native';
import type { MeshStatus } from './MeshStatus';
import { TelinkBleModule } from './TelinkBleModule';

export abstract class RNTelinkBle extends TelinkBleModule {
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
    super();
    this.eventEmitter = eventEmitter;
  }

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
   * Set a scene for all devices
   *
   * @param sceneAddress {number} - Scene mesh address
   */
  public setScene(sceneAddress: number): void {
    this.sendRawString(
      `a3 ff 00 00 00 00 02 00 ff ff 82 46 ${uint16ToHexString(
        sceneAddress
      )} 00`
    );
  }

  /**
   * Remove an existing scene
   *
   * @param sceneAddress {number} - Scene mesh address
   */
  public removeScene(sceneAddress: number): void {
    this.sendRawString(
      `a3 ff 00 00 00 00 02 00 ff ff 82 9E ${uint16ToHexString(
        sceneAddress
      )} 00`
    );
  }

  /**
   * Recall a scene on mesh network
   *
   * @param sceneAddress {number} - Scene mesh address
   */
  public recallScene(sceneAddress: number): void {
    this.sendRawString(
      `a3 ff 00 00 00 00 02 00 ff ff 82 42 ${uint16ToHexString(
        sceneAddress
      )} 00`
    );
  }

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
   * @param callback {(device: DeviceInfo) => void | Promise<void>} - Unprovisioned device handler
   */
  public onDeviceFound(
    callback: (device: DeviceInfo) => void | Promise<void>
  ): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_DEVICE_FOUND,
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

  /**
   * Unprovisioned device scanning finish
   *
   * @param callback {() => void | Promise<void>}
   * @returns {() => void}
   */
  public onAndroidScanFinish(callback: () => void | Promise<void>): () => void {
    const subscription: EventSubscription = this.eventEmitter.addListener(
      BleEvent.EVENT_ANDROID_SCAN_FINISH,
      callback
    );

    return () => {
      subscription.remove();
    };
  }
}
