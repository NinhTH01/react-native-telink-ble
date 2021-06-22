package com.react.telink.ble.helpers

fun ByteArray.toHexString(): String {
  return this.joinToString(":") {
    String.format("%02x", it)
  }
}
