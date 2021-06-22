import type { Device } from 'react-native-telink-ble';

export interface DeviceReducerAction {
  type: 'reset' | 'add';
  device?: Device;
}

export function deviceReducer(
  state: Device[],
  action: DeviceReducerAction
): Device[] {
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
