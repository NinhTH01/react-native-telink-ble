package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class BleDeviceDto : WritableNativeMap() {
  var manufacturerData: String?
    get() = getString(this::manufacturerData.name)
    set(value) {
      putString(this::manufacturerData.name, value)
    }

  var deviceType: String?
    get() = getString(this::deviceType.name)
    set(value) {
      putString(this::deviceType.name, value)
    }

  var version: String?
    get() = getString(this::version.name)
    set(value) {
      putString(this::version.name, value)
    }

  var uuid: String?
    get() = getString(this::uuid.name)
    set(value) {
      putString(this::uuid.name, value)
    }

  var macAddress: String?
    get() = getString(this::macAddress.name)
    set(value) {
      putString(this::macAddress.name, value)
    }

  var meshAddress: Int
    get() = getInt(this::meshAddress.name)
    set(value) {
      putInt(this::meshAddress.name, value)
    }

  var rssi: Int
    get() = getInt(this::rssi.name)
    set(value) {
      putInt(this::rssi.name, value)
    }

  var name: String?
    get() = getString(this::name.name)
    set(value) {
      putString(this::name.name, value)
    }
}
