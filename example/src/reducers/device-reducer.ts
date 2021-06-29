import type { Device, NodeInfo } from 'react-native-telink-ble';

export interface DeviceReducerAction<T extends Device | NodeInfo> {
  type: 'reset' | 'add';
  device?: T;
}

export function deviceReducer<T extends Device | NodeInfo>(
  state: T[],
  action: DeviceReducerAction<T>
): T[] {
  switch (action.type) {
    case 'add':
      if (state.find((d) => d.address === action.device?.address)) {
        return state;
      }
      return [...state, action.device!];

    case 'reset':
      return [];

    default:
      return state;
  }
}
