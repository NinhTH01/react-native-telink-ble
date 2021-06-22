import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import type { Device } from 'react-native-telink-ble';

interface DeviceViewProps {
  device: Device;
}

export default function DeviceView(props: DeviceViewProps) {
  const { device } = props;

  return (
    <View style={styles.container}>
      <Text>{device.address}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    height: 56,
    marginLeft: 16,
    marginRight: 16,
    marginTop: 8,
    marginBottom: 8,
    padding: 16,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1,
    },
    shadowOpacity: 0.22,
    shadowRadius: 2.22,

    elevation: 3,
  },
});
