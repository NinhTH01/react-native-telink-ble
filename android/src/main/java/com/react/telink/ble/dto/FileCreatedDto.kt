package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeMap

class FileCreatedDto : WritableNativeMap() {
  var fileName: String?
    get() = getString(this::fileName.name)
    set(value: String?) {
      putString(this::fileName.name, value)
    }

  var filePath: String?
    get() = getString(this::filePath.name)
    set(value: String?) {
      putString(this::filePath.name, value)
    }
}
