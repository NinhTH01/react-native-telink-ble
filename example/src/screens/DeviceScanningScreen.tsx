import type { StackScreenProps } from '@react-navigation/stack';
import React, { FC, Reducer } from 'react';
import { StyleSheet, View } from 'react-native';
import { ActivityIndicator, Button } from 'react-native-paper';
import type { Device } from 'react-native-telink-ble';
import TelinkBle from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import DeviceView from '../components/DeviceView';
import { deviceReducer, DeviceReducerAction } from '../reducers/device-reducer';

export const DeviceScanningScreen: FC<StackScreenProps<any>> = (
  props: StackScreenProps<any>
) => {
  const { navigation } = props;

  const [scanning, setScanning] = React.useState<boolean>(false);

  const [devices, dispatch] = React.useReducer<
    Reducer<Device[], DeviceReducerAction>
  >(deviceReducer, []);

  const handleStartScanning = React.useCallback(() => {
    setScanning(true);
    TelinkBle.startScanning();
  }, []);

  const handleStopScanning = React.useCallback(() => {
    TelinkBle.stopScanning();
    setScanning(false);
  }, []);

  React.useEffect(() => {
    return navigation.addListener('focus', () => {
      handleStartScanning();
    });
  }, [navigation, handleStartScanning]);

  React.useEffect(() => {
    return TelinkBle.addScanningTimeoutListener(() => {
      handleStopScanning();
    });
  }, [handleStopScanning]);

  React.useEffect(() => {
    return TelinkBle.addDeviceFoundListener((device: Device) => {
      dispatch({
        type: 'add',
        device,
      });
    });
  }, []);

  return (
    <>
      {scanning && (
        <View style={styles.indicatorContainer}>
          <Button onPress={handleStopScanning}>Stop scanning</Button>
          <ActivityIndicator />
        </View>
      )}
      {!scanning && (
        <View style={styles.indicatorContainer}>
          <Button onPress={handleStartScanning}>Start scanning</Button>
        </View>
      )}
      {devices.map((device: Device) => (
        <React.Fragment key={device.address}>
          <DeviceView device={device} />
        </React.Fragment>
      ))}
    </>
  );
};

DeviceScanningScreen.displayName = nameof(DeviceScanningScreen);

export default DeviceScanningScreen;

const styles = StyleSheet.create({
  indicatorContainer: {
    flexDirection: 'row',
  },
});
