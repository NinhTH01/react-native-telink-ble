package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class MeshNetworkDto : WritableNativeMap() {
  var appKey: String?
    get() = getString(this::appKey.name)
    set(value: String?) {
      putString(this::appKey.name, value)
    }

  var netKey: String?
    get() = getString(this::netKey.name)
    set(value: String?) {
      putString(this::netKey.name, value)
    }
}
