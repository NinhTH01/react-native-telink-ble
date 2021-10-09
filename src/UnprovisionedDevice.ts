export interface UnprovisionedDevice {
  uuid?: string;

  macAddress?: string;

  deviceType?: string;

  manufacturerData?: string;

  rssi?: number;
}
