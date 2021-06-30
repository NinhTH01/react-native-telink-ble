import {
  createStackNavigator,
  StackHeaderProps,
} from '@react-navigation/stack';
import type { FC } from 'react';
import React from 'react';
import nameof from 'ts-nameof.macro';
import CustomNavigationBar from '../components/CustomNavigationBar';
import { DeviceControlScreen } from '../screens/DeviceControlScreen';
import DeviceScanningScreen from '../screens/DeviceScanningScreen';
import HomeScreen from '../screens/HomeScreen';

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
