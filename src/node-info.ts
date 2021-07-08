export interface NodeInfo {
  uuid: string;

  address: string;

  unicastId: number;

  bound: boolean;

  pidDesc: string;

  onOffDesc: string;

  deviceKey: string;

  elementCnt: number;

  isDefaultBind: boolean;

  isKeyBindSuccess: boolean;

  security: string;

  lum: number;

  temp: number;

  defaultTTL: number;

  crpl: string;
}
