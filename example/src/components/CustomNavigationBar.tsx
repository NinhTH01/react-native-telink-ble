import { Appbar } from 'react-native-paper';
import React from 'react';
import type { StackHeaderProps } from '@react-navigation/stack';

function CustomNavigationBar({
  scene,
  previous,
  navigation,
}: StackHeaderProps) {
  return (
    <Appbar.Header>
      {previous ? (
        <Appbar.BackAction touchSoundDisabled onPress={navigation.goBack} />
      ) : null}

      <Appbar.Content title={scene.route.name} />
    </Appbar.Header>
  );
}

export default CustomNavigationBar;
