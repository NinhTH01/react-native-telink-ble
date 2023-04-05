package com.react.telink.ble.dto

import com.facebook.react.bridge.WritableNativeArray

class NodeList : WritableNativeArray() {
  fun add(node: NodeInfoDto) {
    pushMap(node)
  }
}
