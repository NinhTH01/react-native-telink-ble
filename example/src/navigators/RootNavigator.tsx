import {
  createStackNavigator,
  StackHeaderProps,
} from '@react-navigation/stack';
import CustomNavigationBar from 'example/src/components/CustomNavigationBar';
import DeviceScanningScreen from 'example/src/screens/DeviceScanningScreen';
import HomeScreen from 'example/src/screens/HomeScreen';
import type { FC } from 'react';
import React from 'react';
import nameof from 'ts-nameof.macro';
import { DeviceControlScreen } from '../screens/DeviceControlScreen';

const { Navigator, Screen } = createStackNavigator();

export const RootNavigator: FC = () => {
  return (
    <Navigator
      initialRouteName={HomeScreen.displayName}
      screenOptions={{
        header: (props: StackHeaderProps) => <CustomNavigationBar {...props} />,
      }}
    >
      {[HomeScreen, DeviceScanningScreen, DeviceControlScreen].map((screen) => (
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
