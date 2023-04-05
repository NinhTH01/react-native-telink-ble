package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class OtaProgressDto : WritableNativeMap() {
  var description: String?
    get() = getString(this::description.name)
    set(value: String?) {
      putString(this::description.name, value)
    }

  var progress: Int
    get() = getInt(this::progress.name)
    set(value) {
      putInt(this::progress.name, value)
    }
}
