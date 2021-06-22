import type { FC } from 'react';
import React from 'react';
import nameof from 'ts-nameof.macro';
import { StyleSheet, View } from 'react-native';
import { FAB } from 'react-native-paper';
import type { StackScreenProps } from '@react-navigation/stack';
import DeviceScanningScreen from 'example/src/screens/DeviceScanningScreen';

export const HomeScreen: FC<Partial<StackScreenProps<any>>> = (
  props: Partial<StackScreenProps<any>>
) => {
  const { navigation } = props;

  return (
    <View style={styles.outerContainer}>
      <FAB
        style={styles.fab}
        small
        icon="plus"
        onPress={() => {
          navigation?.navigate(DeviceScanningScreen.displayName!);
        }}
      />
    </View>
  );
};

HomeScreen.displayName = nameof(HomeScreen);

export default HomeScreen;

const styles = StyleSheet.create({
  outerContainer: {
    width: '100%',
    height: '100%',
    flex: 1,
  },
  fab: {
    margin: 16,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1,
    position: 'absolute',
    right: 0,
    bottom: 0,
  },
});
