package com.react.telink.ble

import android.os.Bundle
import android.os.Handler
import com.facebook.react.ReactActivity
import com.react.telink.ble.model.AppSettings
import com.react.telink.ble.model.MeshInfo
import com.react.telink.ble.model.NodeStatusChangedEvent
import com.react.telink.ble.model.UnitConvert
import com.telink.ble.mesh.core.MeshUtils
import com.telink.ble.mesh.core.message.generic.OnOffGetMessage
import com.telink.ble.mesh.core.message.time.TimeSetMessage
import com.telink.ble.mesh.foundation.Event
import com.telink.ble.mesh.foundation.EventListener
import com.telink.ble.mesh.foundation.MeshConfiguration
import com.telink.ble.mesh.foundation.MeshService
import com.telink.ble.mesh.foundation.event.AutoConnectEvent
import com.telink.ble.mesh.foundation.event.MeshEvent
import com.telink.ble.mesh.foundation.parameter.AutoConnectParameters
import com.telink.ble.mesh.util.MeshLogger

open class BleActivity : ReactActivity(), EventListener<String?>, MeshAutoConnect {
  private val mHandler = Handler()

  private val tag = javaClass.simpleName

  private var app: ReactMeshApplication? = null

  private fun setApplication(app: ReactMeshApplication) {
    this.app = app
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setApplication(BleApplication.getInstance())
    app!!.addEventListener(AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this)
    app!!.addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this)
    app!!.addEventListener(MeshEvent.EVENT_TYPE_MESH_EMPTY, this)
    app!!.addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this)
    app!!.addEventListener(AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this)
    app!!.addEventListener(MeshEvent.EVENT_TYPE_MESH_RESET, this)
    app!!.addEventListener(NodeStatusChangedEvent.EVENT_TYPE_NODE_STATUS_CHANGED, this)
    startMeshService()
    resetNodeState()
    TelinkBleModule.setApplication(app!!)
    TelinkBleModule.getInstance()?.setHandler(Handler())
    TelinkBleModule.getInstance()?.autoConnect()
  }

  override fun onDestroy() {
    super.onDestroy()
    app!!.removeEventListener(this)
    MeshService.getInstance().clear()
  }

  override fun onResume() {
    super.onResume()
    autoConnect()
  }

  override fun autoConnect() {
    MeshService.getInstance().autoConnect(AutoConnectParameters())
  }

  private fun startMeshService() {
    MeshService.getInstance().init(this, app!!)
    val meshConfiguration: MeshConfiguration = app!!.getMeshInfo().convertToConfiguration()
    MeshService.getInstance().setupMeshNetwork(meshConfiguration)
    MeshService.getInstance().checkBluetoothState()
    // set DLE enable
    MeshService.getInstance().resetDELState(SharedPreferenceHelper.isDleEnable(this))
  }

  private fun resetNodeState() {
    val mesh: MeshInfo = app!!.getMeshInfo()
    if (mesh.nodes != null) {
      for (deviceInfo in mesh.nodes!!) {
        deviceInfo.setOnOff(-1)
        deviceInfo.lum = 0
        deviceInfo.temp = 0
      }
    }
  }

  override fun performed(event: Event<String?>?) {
    when {
      event!!.type == MeshEvent.EVENT_TYPE_MESH_EMPTY -> {
        MeshLogger.log("$tag#EVENT_TYPE_MESH_EMPTY")
      }
      event.type == AutoConnectEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN -> {
        // get all device on off status when auto connect success
        AppSettings.ONLINE_STATUS_ENABLE = MeshService.getInstance().onlineStatus
        if (!AppSettings.ONLINE_STATUS_ENABLE) {
          MeshService.getInstance().onlineStatus
          val rspMax: Int = app!!.getMeshInfo().onlineCountInAll
          val appKeyIndex: Int = app!!.getMeshInfo().getDefaultAppKeyIndex()
          val message = OnOffGetMessage.getSimple(0xFFFF, appKeyIndex, rspMax)
          MeshService.getInstance().sendMeshMessage(message)
        }
        sendTimeStatus()
      }
      event.type == MeshEvent.EVENT_TYPE_DISCONNECTED -> {
        mHandler.removeCallbacksAndMessages(null)
      }
    }
  }

  private fun sendTimeStatus() {
    mHandler.postDelayed({
      val time = MeshUtils.getTaiTime()
      val offset: Int = UnitConvert.zoneOffset
      val address = 0xFFFF
      val meshInfo: MeshInfo = app!!.getMeshInfo()
      val timeSetMessage =
        TimeSetMessage.getSimple(address, meshInfo.getDefaultAppKeyIndex(), time, offset, 1)
      timeSetMessage.setAck(false)
      MeshService.getInstance().sendMeshMessage(timeSetMessage)
    }, 1500)
  }
}
