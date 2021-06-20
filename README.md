# react-native-telink-ble

Telink BLE bridge for React Native

## Installation

```sh
npm install react-native-telink-ble
```

or using yarn:

```sh
yarn add react-native-telink-ble
```

### Android

#### Add permissions to AndroidManifest.xml
```xml
<manifest>

  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

  <application
    >
    //
  </application>

</manifest>
```
#### Enable Kotlin support for your project (Using Android Studio)
#### Convert your MainApplication and MainActivity to Kotlin (Using Android Studio)
#### In MainApplication:

```kotlin
package com.example.reactnativetelinkble

import android.app.Application
import android.content.Context
import com.facebook.react.*
import com.facebook.soloader.SoLoader
import java.lang.reflect.InvocationTargetException
// Add this ->
import android.os.Handler
import android.os.HandlerThread
import com.reactnativetelinkble.TelinkBleModule
import com.reactnativetelinkble.TelinkBlePackage
import com.telink.ble.mesh.foundation.EventBus
// <- Add this

class MainApplication : Application(), ReactApplication {
  private var mOfflineCheckHandler: Handler? = null // <- Add this

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this,  /* native exopackage */false)
    initializeFlipper(
      this,
      reactNativeHost.reactInstanceManager
    ) // Remove this line if you don't want Flipper enabled

    // Add this ->
    val offlineCheckThread = HandlerThread("offline check thread")
    offlineCheckThread.start()
    mOfflineCheckHandler = Handler(offlineCheckThread.looper)
    TelinkBleModule.setEventBus(EventBus())
  }
  // <- Add this
  // ...
}
```

#### In android/settings.gradle

```gradle
include ':react-native-telink-ble'
project(':react-native-telink-ble').projectDir = new File(rootProject.projectDir, '../../android')
include ':TelinkBleMeshLib'
project(':TelinkBleMeshLib').projectDir = new File(project(':react-native-telink-ble').projectDir, 'libs/TelinkBleMeshLib')
```

#### In android/app/build.gradle

```gradle
dependencies {
  // ...
  implementation project(':react-native-telink-ble')
  implementation project(':TelinkBleMeshLib')
}
```

#### Reload your project

## Usage

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
