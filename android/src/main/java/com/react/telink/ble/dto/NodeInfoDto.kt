package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class NodeInfoDto : WritableNativeMap() {
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

  var deviceKey: String?
    get() = getString(this::deviceKey.name)
    set(value) {
      putString(this::deviceKey.name, value)
    }
}
