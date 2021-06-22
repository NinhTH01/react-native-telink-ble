import 'react-native-gesture-handler';
import { enableScreens } from 'react-native-screens';
import { AppRegistry } from 'react-native';
import { name as appName } from './app.json';
import type { FC, LazyExoticComponent } from 'react';
import React, { Suspense } from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer } from '@react-navigation/native';
import { appStorage } from 'example/src/app/app-storage';
import { globalState } from 'example/src/app/global-state';
import TelinkBle from 'react-native-telink-ble';
import { showError, showInfo } from 'example/src/helpers/toast';
import { Provider as PaperProvider } from 'react-native-paper';

enableScreens();

const App: LazyExoticComponent<FC> = React.lazy(async () => {
  await appStorage.initialize();
  await globalState.initialize();

  if (__DEV__) {
    require('reactn-devtools').default();
  }

  try {
    let networkKey: string = appStorage.networkKey ?? '';
    if (!networkKey) {
      networkKey = await TelinkBle.createMeshNetwork();
      await appStorage.setNetworkKey(networkKey);
    }
    await TelinkBle.initMeshNetwork(networkKey);
    showInfo('Mesh network initialized');
  } catch (error) {
    showError('Can not initialize mesh network');
  }

  return import('example/src/navigators/RootNavigator');
});

function AppEntry() {
  return (
    <PaperProvider>
      <NavigationContainer>
        <SafeAreaProvider>
          <Suspense fallback={null}>
            <App />
          </Suspense>
        </SafeAreaProvider>
      </NavigationContainer>
    </PaperProvider>
  );
}

AppRegistry.registerComponent(appName, () => AppEntry);
