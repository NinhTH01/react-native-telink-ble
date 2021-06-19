import { AppRegistry } from 'react-native';
import { name as appName } from './app.json';
import type { FC, LazyExoticComponent } from 'react';
import React, { Suspense } from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer } from '@react-navigation/native';
import { appStorage } from './src/app-storage';

const App: LazyExoticComponent<FC> = React.lazy(async () => {
  await appStorage.initialize();
  return import('./src/App');
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
