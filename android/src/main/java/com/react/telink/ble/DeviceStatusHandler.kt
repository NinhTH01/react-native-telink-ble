package com.react.telink.ble

import com.react.telink.ble.model.NodeInfo

interface DeviceStatusHandler {
  fun onOnOffStatus(nodeInfo: NodeInfo, onOff: Int)

  fun onBrightnessStatus(nodeInfo: NodeInfo, lum: Int)

  fun onTemperatureStatus(nodeInfo: NodeInfo, temp: Int)

  fun onHSLStatus(nodeInfo: NodeInfo, hue: Int, saturation: Int, luminance: Int)
}
