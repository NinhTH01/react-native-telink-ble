import { createStackNavigator } from '@react-navigation/stack';
import type { FC } from 'react';
import React from 'react';
import TelinkBle, { BleEvent } from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import { DeviceControlScreen } from '../screens/DeviceControlScreen';
import DeviceScanningScreen from '../screens/DeviceScanningScreen';
import SceneCreateScreen from '../screens/SceneCreateScreen';
import TabNavigator from './TabNavigator';

const { Navigator, Screen } = createStackNavigator();

export const RootNavigator: FC = () => {
  React.useEffect(() => {
    return TelinkBle.addEventListener(
      BleEvent.EVENT_DEVICE_ON_OFF_STATUS,
      (data) => {
        console.log(data);
      }
    );
  }, []);

  React.useEffect(() => {
    return TelinkBle.addEventListener(
      BleEvent.EVENT_DEVICE_LIGHTNESS,
      (data) => {
        console.log(data);
      }
    );
  }, []);

  React.useEffect(() => {
    return TelinkBle.addEventListener(BleEvent.EVENT_DEVICE_HSL, (data) => {
      console.log(data);
    });
  }, []);

  return (
    <Navigator initialRouteName={TabNavigator.displayName} headerMode="none">
      {[
        TabNavigator,
        DeviceScanningScreen,
        DeviceControlScreen,
        SceneCreateScreen,
      ].map((screen) => (
        <Screen
          key={screen.displayName}
          name={screen.displayName!}
          component={screen}
        />
      ))}
    </Navigator>
  );
};

RootNavigator.displayName = nameof(RootNavigator);

export default RootNavigator;
