export interface BoundDevice {
  unicastId: number;

  bearer: string;

  uuid: string;

  isDefaultBound: boolean;

  appKeyIndex: number;
}
