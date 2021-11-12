# react-native-telink-ble

BLE Mesh library for Telink IoT devices, built for React Native

## Installation

```sh
yarn add react-native-telink-ble
```

Auto linking is available, but you need additional config steps.

### Android

- Add [Kotlin support](https://developer.android.com/kotlin/add-kotlin) for your project
- Using Android Studio, convert your `MainActivity` and `MainApplication` to Kotlin.
- Add Bluetooth permissions to `AndroidManifest.xml`:

    ```xml
    <uses-permission
        android:name="android.permission.BLUETOOTH"
        android:maxSdkVersion="30" />
    <uses-permission
        android:name="android.permission.BLUETOOTH_ADMIN"
        android:maxSdkVersion="30" />
    <!-- Needed only if your app looks for Bluetooth devices.
        You must add an attribute to this permission, or declare the
        ACCESS_FINE_LOCATION permission, depending on the results when you
        check location usage in your app. -->
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <!-- Needed only if your app makes the device discoverable to Bluetooth
        devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <!-- Needed only if your app communicates with already-paired Bluetooth
        devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    ```
- Add `TelinkBleMeshLib` to `settings.gradle`:

    ```gradle
    include ':TelinkBleMeshLib'
    project(':TelinkBleMeshLib').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-telink-ble/TelinkBleMeshLib')
    ```
- Add module implementations:

    ```gradle
    dependencies {
        // ...

        implementation project(":TelinkBleMeshLib")
        implementation project(":react-native-telink-ble")
    }
    ```

- Disable autolink:

```js
/**
 * @format
 */

module.exports = {
    // ...
    dependencies: {
        // ...
        'react-native-telink-ble': {
            platforms: {
                android: null,
                ios: null,
            },
        },
    },
};

```
### iOS

- Add pods:
    ```Podfile
    target 'YourApp' do
        pod 'RNTelinkBle', :path => '../node_modules/react-native-telink-ble'
        pod 'TelinkSigMeshLib', :path => '../node_modules/react-native-telink-ble'
    end
    ```

- Then do pod install:

    ```sh
    cd ios && pod install && cd ..
    ```

- Edit `AppDelegate.m`:

    ```m

    ```

## Usage

[Updating]

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
