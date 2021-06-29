import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableOpacity,
  TouchableOpacityProps,
  View,
} from 'react-native';
import type { Device } from 'react-native-telink-ble';
import TelinkBle from 'react-native-telink-ble';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import type { BoundDevice } from 'src/bound-device';

interface DeviceViewProps extends TouchableOpacityProps {
  device: Device;

  index: number;
}

export default function DeviceView(props: DeviceViewProps) {
  const { device, ...restProps } = props;

  const handleProvision = React.useCallback(() => {
    TelinkBle.startProvisioning(device.uuid).then((unicastId: number) => {
      console.log(unicastId);
    });
  }, [device.uuid]);

  React.useEffect(() => {
    return TelinkBle.addBindingSuccessListener((d: BoundDevice) => {
      console.log(d);
    });
  }, []);

  return (
    <TouchableOpacity
      style={styles.container}
      {...restProps}
      onPress={handleProvision}
    >
      <View style={styles.head}>
        <MaterialIcons name="laptop" size={20} style={styles.icon} />
        <Text>MAC</Text>
      </View>
      <Text>{device.address}</Text>

      <View style={styles.deviceInfo}>
        <MaterialIcons name="tag" size={20} style={styles.icon} />
        <Text>UUID</Text>
      </View>
      <Text>{device.uuid}</Text>

      <View style={styles.deviceInfo}>
        <MaterialIcons name="tag" size={20} style={styles.icon} />
        <Text>DeviceType</Text>
      </View>
      <Text>{device.scanRecord.split(':').slice(49, 52).join(':')}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    marginLeft: 16,
    marginRight: 16,
    marginTop: 8,
    marginBottom: 8,
    paddingTop: 8,
    paddingBottom: 8,
    paddingLeft: 16,
    paddingRight: 16,
    borderRadius: 8,
    backgroundColor: '#FFF',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 3,
    },
    shadowOpacity: 0.27,
    shadowRadius: 4.65,
    elevation: 6,
    overflow: 'hidden',
  },
  head: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 4,
    marginBottom: 4,
  },
  icon: {
    marginRight: 12,
  },
  deviceInfo: {
    flexGrow: 1,
    flexDirection: 'row',
    marginTop: 4,
    marginBottom: 4,
  },
});
