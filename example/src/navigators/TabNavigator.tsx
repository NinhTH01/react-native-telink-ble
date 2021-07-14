import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import type { FC } from 'react';
import React from 'react';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import nameof from 'ts-nameof.macro';
import GroupScreen from '../screens/GroupScreen';
import HomeScreen from '../screens/HomeScreen';
import SceneScreen from '../screens/SceneScreen';

const { Navigator, Screen } = createBottomTabNavigator();

export const TabNavigator: FC = () => {
  return (
    <Navigator initialRouteName={HomeScreen.displayName}>
      <Screen
        key={HomeScreen.displayName}
        name={HomeScreen.displayName!}
        component={HomeScreen}
        options={{
          tabBarIcon({ color, size }) {
            return <MaterialIcons name="home" size={size} color={color} />;
          },
        }}
      />
      <Screen
        key={GroupScreen.displayName}
        name={GroupScreen.displayName!}
        component={GroupScreen}
        options={{
          tabBarIcon({ color, size }) {
            return <MaterialIcons name="stream" size={size} color={color} />;
          },
        }}
      />
      <Screen
        key={SceneScreen.displayName}
        name={SceneScreen.displayName!}
        component={SceneScreen}
        options={{
          tabBarIcon({ color, size }) {
            return <MaterialIcons name="insights" size={size} color={color} />;
          },
        }}
      />
    </Navigator>
  );
};

TabNavigator.displayName = nameof(TabNavigator);

export default TabNavigator;
