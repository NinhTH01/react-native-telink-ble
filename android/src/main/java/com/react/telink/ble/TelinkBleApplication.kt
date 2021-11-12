package com.react.telink.ble

import android.os.Handler
import android.os.HandlerThread
import com.facebook.react.ReactApplication
import com.react.telink.ble.model.*
import com.telink.ble.mesh.core.message.MeshSigModel
import com.telink.ble.mesh.core.message.generic.LevelStatusMessage
import com.telink.ble.mesh.core.message.generic.OnOffStatusMessage
import com.telink.ble.mesh.core.message.lighting.CtlStatusMessage
import com.telink.ble.mesh.core.message.lighting.CtlTemperatureStatusMessage
import com.telink.ble.mesh.core.message.lighting.HslStatusMessage
import com.telink.ble.mesh.core.message.lighting.LightnessStatusMessage
import com.telink.ble.mesh.foundation.MeshApplication
import com.telink.ble.mesh.foundation.event.MeshEvent
import com.telink.ble.mesh.foundation.event.NetworkInfoUpdateEvent
import com.telink.ble.mesh.foundation.event.OnlineStatusEvent
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent
import com.telink.ble.mesh.util.FileSystem
import com.telink.ble.mesh.util.MeshLogger
import kotlin.math.roundToInt

abstract class TelinkBleApplication : MeshApplication(), ReactApplication {
  private var meshInfo: MeshInfo? = null

  private var mOfflineCheckHandler: Handler? = null

  open fun getOfflineCheckHandler(): Handler? {
    return mOfflineCheckHandler
  }

  override fun onCreate() {
    super.onCreate()
    reactApplication = this
    // 2018-11-20T10:05:20-08:00
    // 2020-07-27T15:15:29+08:00
    val offlineCheckThread = HandlerThread("offline check thread")
    offlineCheckThread.start()
    mOfflineCheckHandler = Handler(offlineCheckThread.looper)
    initMesh()
    MeshLogger.enableRecord(SharedPreferenceHelper.isLogEnable(this))
    MeshLogger.d(meshInfo.toString())
    AppCrashHandler.init(this)
  }

  private fun initMesh() {
    val configObj = FileSystem.readAsObject(this, MeshInfo.FILE_NAME)
    if (configObj == null) {
      meshInfo = MeshInfo.createNewMesh(this)
      meshInfo!!.saveOrUpdate(this)
    } else {
      meshInfo = configObj as MeshInfo
    }
  }

  open fun getMeshInfo(): MeshInfo? {
    return meshInfo
  }

  override fun onNetworkInfoUpdate(networkInfoUpdateEvent: NetworkInfoUpdateEvent?) {
    meshInfo!!.ivIndex = networkInfoUpdateEvent!!.ivIndex
    meshInfo!!.sequenceNumber = networkInfoUpdateEvent.sequenceNumber
    meshInfo!!.saveOrUpdate(this)
  }

  override fun onStatusNotificationEvent(statusNotificationEvent: StatusNotificationEvent?) {
    val message = statusNotificationEvent!!.notificationMessage

    val statusMessage = message.statusMessage
    if (statusMessage != null) {
      var statusChangedNode: NodeInfo? = null
      if (message.statusMessage is OnOffStatusMessage) {
        val onOffStatusMessage = statusMessage as OnOffStatusMessage
        val onOff =
          if (onOffStatusMessage.isComplete) onOffStatusMessage.targetOnOff.toInt() else onOffStatusMessage.presentOnOff.toInt()
        for (nodeInfo in meshInfo!!.nodes) {
          if (nodeInfo.meshAddress == message.src) {
            if (nodeInfo.onOff != onOff) {
              statusChangedNode = nodeInfo
            }
            nodeInfo.onOff = onOff
            TelinkBleModule.getInstance()?.onOnOffStatus(nodeInfo, onOff)
            break
          }
        }
      } else if (message.statusMessage is LevelStatusMessage) {
        val levelStatusMessage = statusMessage as LevelStatusMessage
        val srcAdr = message.src
        val level =
          if (levelStatusMessage.isComplete) levelStatusMessage.targetLevel else levelStatusMessage.presentLevel
        val tarVal: Int = UnitConvert.level2lum(level.toShort())
        for (onlineDevice in meshInfo!!.nodes) {
          if (onlineDevice.compositionData == null) {
            continue
          }
          val lightnessEleAdr =
            onlineDevice.getTargetEleAdr(MeshSigModel.SIG_MD_LIGHTNESS_S.modelId)
          if (lightnessEleAdr == srcAdr) {
            if (onLumStatus(onlineDevice, tarVal)) {
              statusChangedNode = onlineDevice
            }
          } else {
            val tempEleAdr =
              onlineDevice.getTargetEleAdr(MeshSigModel.SIG_MD_LIGHT_CTL_TEMP_S.modelId)
            if (tempEleAdr == srcAdr) {
              if (onlineDevice.temp != tarVal) {
                statusChangedNode = onlineDevice
                onlineDevice.temp = tarVal
              }
            }
          }
        }
      } else if (message.statusMessage is CtlStatusMessage) {
        val ctlStatusMessage = statusMessage as CtlStatusMessage
        MeshLogger.d("ctl : $ctlStatusMessage")
        val srcAdr = message.src
        for (onlineDevice in meshInfo!!.nodes) {
          if (onlineDevice.meshAddress == srcAdr) {
            val lum =
              if (ctlStatusMessage.isComplete)
                ctlStatusMessage.targetLightness
              else
                ctlStatusMessage.presentLightness
            if (onLumStatus(onlineDevice, UnitConvert.lightness2lum(lum))) {
              statusChangedNode = onlineDevice
            }
            val temp =
              if (ctlStatusMessage.isComplete)
                ctlStatusMessage.targetTemperature
              else
                ctlStatusMessage.presentTemperature
            if (onTempStatus(onlineDevice, UnitConvert.tempToTemp100(temp))) {
              statusChangedNode = onlineDevice
            }

            break
          }
        }
      } else if (message.statusMessage is LightnessStatusMessage) {
        val lightnessStatusMessage = statusMessage as LightnessStatusMessage
        val srcAdr = message.src
        for (onlineDevice in meshInfo!!.nodes) {
          if (onlineDevice.meshAddress == srcAdr) {
            val brightness =
              if (lightnessStatusMessage.isComplete) lightnessStatusMessage.targetLightness else lightnessStatusMessage.presentLightness
            if (onLumStatus(onlineDevice, UnitConvert.lightness2lum(brightness))) {
              statusChangedNode = onlineDevice
            }
            TelinkBleModule.getInstance()?.onBrightnessStatus(onlineDevice, UnitConvert.lightness2lum(brightness))
            break
          }
        }
      } else if (message.statusMessage is CtlTemperatureStatusMessage) {
        val ctlTemp = statusMessage as CtlTemperatureStatusMessage
        val srcAdr = message.src
        for (onlineDevice in meshInfo!!.nodes) {
          if (onlineDevice.meshAddress == srcAdr) {
            val temp =
              if (ctlTemp.isComplete) ctlTemp.targetTemperature else ctlTemp.presentTemperature
            if (onTempStatus(onlineDevice, UnitConvert.lightness2lum(temp))) {
              statusChangedNode = onlineDevice
            }
            TelinkBleModule.getInstance()
              ?.onTemperatureStatus(onlineDevice, UnitConvert.tempToTemp100(UnitConvert.lightness2lum(temp)))
            break
          }
        }
      } else if (message.statusMessage is HslStatusMessage) {
        val hslStatusMessage = statusMessage as HslStatusMessage
        val srcAdr = message.src
        for (onlineDevice in meshInfo!!.nodes) {
          if (onlineDevice.meshAddress == srcAdr) {
            val hue = hslStatusMessage.hue
            val saturation = hslStatusMessage.saturation
            val lightness = hslStatusMessage.lightness
            TelinkBleModule.getInstance()
              ?.onHSLStatus(
                onlineDevice,
                (hue.toDouble() / 0xFFFF * 360).roundToInt(),
                UnitConvert.tempToTemp100(saturation),
                UnitConvert.tempToTemp100(lightness)
              )
            break
          }
        }
      }
      statusChangedNode?.let { onNodeInfoStatusChanged(it) }
    }
  }

  private fun onLumStatus(nodeInfo: NodeInfo, lum: Int): Boolean {
    var statusChanged = false
    val tarOnOff = if (lum > 0) 1 else 0
    if (nodeInfo.onOff != tarOnOff) {
      statusChanged = true
    }
    nodeInfo.onOff = tarOnOff
    if (nodeInfo.lum != lum) {
      statusChanged = true
      nodeInfo.lum = lum
    }
    return statusChanged
  }

  private fun onTempStatus(nodeInfo: NodeInfo, temp: Int): Boolean {
    var statusChanged = false
    if (nodeInfo.temp != temp) {
      statusChanged = true
      nodeInfo.temp = temp
    }
    return statusChanged
  }

  override fun onOnlineStatusEvent(onlineStatusEvent: OnlineStatusEvent?) {
    val infoList = onlineStatusEvent!!.onlineStatusInfoList
    if (infoList != null && meshInfo != null) {
      var statusChangedNode: NodeInfo? = null
      for (onlineStatusInfo in infoList) {
        if (onlineStatusInfo.status == null || onlineStatusInfo.status.size < 3) break
        val deviceInfo = meshInfo!!.getDeviceByMeshAddress(onlineStatusInfo.address)
          ?: continue
        val onOff: Int = if (onlineStatusInfo.sn.toInt() == 0) {
          -1
        } else {
          if (onlineStatusInfo.status[0].toInt() == 0) {
            0
          } else {
            1
          }
        }
        if (deviceInfo.onOff != onOff) {
          statusChangedNode = deviceInfo
        }
        deviceInfo.onOff = onOff
        if (deviceInfo.lum != onlineStatusInfo.status[0].toInt()) {
          statusChangedNode = deviceInfo
          deviceInfo.lum = onlineStatusInfo.status[0].toInt()
        }
        if (deviceInfo.temp != onlineStatusInfo.status[1].toInt()) {
          statusChangedNode = deviceInfo
          deviceInfo.temp = onlineStatusInfo.status[1].toInt()
        }
      }
      statusChangedNode?.let { onNodeInfoStatusChanged(it) }
    }
  }

  /**
   * node info status changed for UI refresh
   */
  private fun onNodeInfoStatusChanged(nodeInfo: NodeInfo) {
    dispatchEvent(
      NodeStatusChangedEvent(
        this,
        NodeStatusChangedEvent.EVENT_TYPE_NODE_STATUS_CHANGED,
        nodeInfo
      )
    )
  }

  override fun onMeshEvent(meshEvent: MeshEvent?) {
    val eventType: String = meshEvent!!.type
    if (MeshEvent.EVENT_TYPE_DISCONNECTED == eventType) {
      AppSettings.ONLINE_STATUS_ENABLE = false
      for (nodeInfo in meshInfo!!.nodes) {
        nodeInfo.onOff = NodeInfo.ON_OFF_STATE_OFFLINE
      }
    }
  }

  companion object {
    private lateinit var reactApplication: TelinkBleApplication

    fun getInstance(): TelinkBleApplication {
      return reactApplication
    }
  }
}
