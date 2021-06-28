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

#### Make MainApplication inherits BleApplication:

```kotlin
package com.example.reactnativetelinkble

// import android.app.Application
import android.content.Context
import com.facebook.react.*
import com.facebook.soloader.SoLoader
import java.lang.reflect.InvocationTargetException
// Add this ->
import com.react.telink.ble.BleApplication
import com.react.telink.ble.TelinkBlePackage
// <- Add this

// class MainApplication : Application(), ReactApplication {
class MainApplication : BleApplication(), ReactApplication {
  // MainApplication inherits BleApplication instead of android.app.Application
  // ...
}
```

#### Make MainActivity inherits BleActivity:

```kotlin
// import android.app.Activity
import com.react.telink.ble.BleActivity

class MainActivity : BleActivity() {
  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
  override fun getMainComponentName(): String {
    return "TelinkBleExample"
  }
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

#### Reload project

## Usage

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
