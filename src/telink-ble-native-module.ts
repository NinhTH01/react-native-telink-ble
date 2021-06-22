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
   *
   * @param networkKey {string} - Mesh network key
   */
  initMeshNetwork(networkKey: string): Promise<string>;

  /**
   * Create mesh network
   *
   * @return {Promise<string>} - A promise resolves to mesh network key in string
   */
  createMeshNetwork(): Promise<string>;
}
