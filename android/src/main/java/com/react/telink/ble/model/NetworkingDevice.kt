package com.react.telink.ble.model

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import com.telink.ble.mesh.util.LogInfo
import com.telink.ble.mesh.util.MeshLogger
import java.util.*

class NetworkingDevice(var nodeInfo: NodeInfo) {
  var state: NetworkingState = NetworkingState.IDLE
  var bluetoothDevice: BluetoothDevice? = null
  var logs: MutableList<LogInfo> = ArrayList()
  var logExpand = false
  val stateColor: Int
    get() = Color.YELLOW
  val isProcessing: Boolean
    get() = state === NetworkingState.PROVISIONING || state === NetworkingState.BINDING || state === NetworkingState.TIME_PUB_SETTING

  fun addLog(tag: String?, log: String?) {
    logs.add(LogInfo(tag, log, MeshLogger.LEVEL_DEBUG))
  }

  companion object {
    const val TAG_SCAN = "scan"
    const val TAG_PROVISION = "provision"
    const val TAG_BIND = "bind"
    const val TAG_PUB_SET = "pub-set"
  }
}
