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
// import com.facebook.react.*
import com.react.telink.ble.BleActivity

class MainActivity : BleActivity() {
  // ...
}

```

#### In android/settings.gradle

```gradle
include ':TelinkBleMeshLib'
project(':TelinkBleMeshLib').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-telink-ble/android/libs/TelinkBleMeshLib')

include ':react-native-telink-ble'
project(':react-native-telink-ble').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-telink-ble/android')
```

#### In android/app/build.gradle

```gradle
dependencies {
  // ...
  implementation project(':TelinkBleMeshLib')
  implementation project(':react-native-telink-ble')
}
```

#### Reload project

### iOS

#### Define your permissions usage in Info.plist:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <!-- ... -->
  <key>NSLocationWhenInUseUsageDescription</key>
	<string>App needs location permission to find and connect to other devices around you</string>
	<key>NSBluetoothAlwaysUsageDescription</key>
	<string>App needs bluetooth permission to find and connect to other devices on your network</string>
	<key>NSBluetoothPeripheralUsageDescription</key>
	<string>App needs bluetooth permission to find and connect to other devices on your network</string>
   <!-- ... -->
</dict>
```

#### Manual linking

Add to Podfile:

```ruby
  pod 'RNTelinkBle', :path => '../node_modules/react-native-telink-ble'
  pod 'TelinkSigMeshLib', :path => '../node_modules/react-native-telink-ble/TelinkSigMeshLib'
  pod 'OpenSSL-Universal'
```

Then run `pod install` in ios directory

## Usage

**`startMeshSDK` (iOS Only)**

Call this method once, not in component lifecycle to initialize mesh sdk on iOS

---

**`startScanning(): void`**

Start device scanning.

---

**`stopScanning(): void`**

Stop device scanning (Android only)

---

**`setHsl(unicastId: number, hsl: {h: number; s: number; l: number}): void`**

Set HSL values for RGB devices

`h`: in range 0..359

`s`: in range 0..100

`l`: in range 0..100

---

**`setOnOff(unicastId: number, onOff: 0 | 1): void`**

Set device on-off status (0: ON, 1: OFF)

---

**`setAllOff(): void`**

Turn off all devices on mesh network

---

**`setAllOn(): void`**

Turn on all devices on mesh network

---

**`setLuminance(unicastId: number, luminance: number): void`**

Set luminance (dimming) for device: luminance value in range 0..100

---

**`setTemp(unicastId: number, temp: number): void`**

Set temperature (CTL) for device: temp value in range 0..100

---

**`kickOut(unicastId: number): void`**

Reset a node with address: `unicastId`

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
