import * as React from 'react';
import { StyleSheet, View } from 'react-native';
import { Button } from 'react-native-paper';
import TelinkBle from 'react-native-telink-ble';
import { appStorage } from './app-storage';

export default function App() {
  const [loading, setLoading] = React.useState<boolean>(false);

  const handleNetwork = React.useCallback(async () => {
    let networkKey: string = appStorage.networkKey ?? '';
    if (!networkKey) {
      networkKey = await TelinkBle.createMeshNetwork();
      await appStorage.setNetworkKey(networkKey);
    }
    await TelinkBle.initMeshNetwork(networkKey);
  }, []);

  React.useEffect(() => {
    handleNetwork();
  }, [handleNetwork]);

  function handleScan() {
    setLoading(true);
    TelinkBle.startScanning();
  }

  return (
    <View style={styles.container}>
      {!loading && (
        <>
          <Button onPress={handleScan}>Start scanning</Button>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
