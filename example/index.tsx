import { NavigationContainer } from '@react-navigation/native';
import type { FC, LazyExoticComponent } from 'react';
import React, { Suspense } from 'react';
import { AppRegistry, Platform } from 'react-native';
import 'react-native-gesture-handler';
import { PERMISSIONS, request } from 'react-native-permissions';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { enableScreens } from 'react-native-screens';
import 'react-native-telink-ble';
import TelinkBle from 'react-native-telink-ble';
import { name as appName } from './app.json';
import { globalState } from './src/app/global-state';
import { showError } from './src/helpers/toast';

enableScreens();

const App: LazyExoticComponent<FC> = React.lazy(async () => {
  TelinkBle.startMeshSDK();

  await globalState.initialize();

  if (__DEV__) {
    require('reactn-devtools').default();
  }

  await request(
    Platform.select({
      android: PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
      ios: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE,
    })!
  )
    .then((status) => {
      if (status === 'granted') {
        return;
      }
      showError('Please enable location permission to find bluetooth devices');
    })
    .catch((error: Error) => {
      console.log(error);
    });

  return import('./src/navigators/RootNavigator');
});

function AppEntry() {
  return (
    <NavigationContainer>
      <SafeAreaProvider>
        <Suspense fallback={null}>
          <App />
        </Suspense>
      </SafeAreaProvider>
    </NavigationContainer>
  );
}

AppRegistry.registerComponent(appName, () => AppEntry);
