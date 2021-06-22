import type { FC } from 'react';
import React from 'react';
import {
  createStackNavigator,
  StackHeaderProps,
} from '@react-navigation/stack';
import nameof from 'ts-nameof.macro';
import DeviceScanningScreen from 'example/src/screens/DeviceScanningScreen';
import HomeScreen from 'example/src/screens/HomeScreen';
import CustomNavigationBar from 'example/src/components/CustomNavigationBar';

const { Navigator, Screen } = createStackNavigator();

export const RootNavigator: FC = () => {
  return (
    <Navigator
      initialRouteName={HomeScreen.displayName}
      screenOptions={{
        header: (props: StackHeaderProps) => <CustomNavigationBar {...props} />,
      }}
    >
      <Screen name={HomeScreen.displayName!} component={HomeScreen} />
      <Screen
        name={DeviceScanningScreen.displayName!}
        component={DeviceScanningScreen}
      />
    </Navigator>
  );
};

RootNavigator.displayName = nameof(RootNavigator);

export default RootNavigator;
