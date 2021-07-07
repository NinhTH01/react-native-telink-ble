import type { StackScreenProps } from '@react-navigation/stack';
import React, { FC, Reducer } from 'react';
import { FlatList, ListRenderItemInfo, StyleSheet, View } from 'react-native';
import { ActivityIndicator, Button } from 'react-native-paper';
import TelinkBle, { Device } from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import DeviceView from '../components/DeviceView';
import { deviceReducer, DeviceReducerAction } from '../reducers/device-reducer';

export const DeviceScanningScreen: FC<StackScreenProps<any>> = (
  props: StackScreenProps<any>
) => {
  const { navigation } = props;

  const [scanning, setScanning] = React.useState<boolean>(false);

  const [devices, dispatch] = React.useReducer<
    Reducer<Device[], DeviceReducerAction<Device>>
  >(deviceReducer, []);

  const handleStartScanning = React.useCallback(() => {
    dispatch({
      type: 'reset',
    });
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
    <FlatList
      contentContainerStyle={styles.listContent}
      ListHeaderComponent={
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
        </>
      }
      data={devices}
      keyExtractor={(device: Device) => device.address}
      renderItem={({ item, index }: ListRenderItemInfo<Device>) => {
        return (
          <React.Fragment key={item.address}>
            <DeviceView device={item} index={index} />
          </React.Fragment>
        );
      }}
    />
  );
};

DeviceScanningScreen.displayName = nameof(DeviceScanningScreen);

export default DeviceScanningScreen;

const styles = StyleSheet.create({
  indicatorContainer: {
    flexDirection: 'row',
  },
  listContent: {
    paddingTop: 8,
    paddingBottom: 8,
  },
});
