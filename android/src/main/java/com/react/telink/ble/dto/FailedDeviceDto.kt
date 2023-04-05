package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class FailedDeviceDto : WritableNativeMap() {
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

  var groupAddress: Int
    get() = getInt(this::groupAddress.name)
    set(value) {
      putInt(this::groupAddress.name, value)
    }
}
