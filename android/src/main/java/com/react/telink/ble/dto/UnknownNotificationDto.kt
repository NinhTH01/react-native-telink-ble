package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class UnknownNotificationDto : WritableNativeMap() {
  var src: Int
    get() = getInt(this::src.name)
    set(value) {
      putInt(this::src.name, value)
    }

  var dst: Int
    get() = getInt(this::dst.name)
    set(value) {
      putInt(this::dst.name, value)
    }

  var opcode: Int
    get() = getInt(this::opcode.name)
    set(value) {
      putInt(this::opcode.name, value)
    }

  var params: String?
    get() = getString(this::params.name)
    set(value) {
      putString(this::params.name, value)
    }
}
