package com.react.telink.ble

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.react.telink.ble.model.*
import com.telink.ble.mesh.core.MeshUtils
import com.telink.ble.mesh.core.access.BindingBearer
import com.telink.ble.mesh.core.message.MeshMessage
import com.telink.ble.mesh.core.message.MeshSigModel
import com.telink.ble.mesh.core.message.config.ConfigStatus
import com.telink.ble.mesh.core.message.config.ModelPublicationSetMessage
import com.telink.ble.mesh.core.message.config.ModelPublicationStatusMessage
import com.telink.ble.mesh.core.message.config.NodeResetMessage
import com.telink.ble.mesh.core.message.generic.OnOffSetMessage
import com.telink.ble.mesh.core.message.lighting.CtlTemperatureSetMessage
import com.telink.ble.mesh.core.message.lighting.HslSetMessage
import com.telink.ble.mesh.core.message.lighting.LightnessSetMessage
import com.telink.ble.mesh.entity.*
import com.telink.ble.mesh.foundation.Event
import com.telink.ble.mesh.foundation.MeshService
import com.telink.ble.mesh.foundation.event.BindingEvent
import com.telink.ble.mesh.foundation.event.ProvisioningEvent
import com.telink.ble.mesh.foundation.event.ScanEvent
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent
import com.telink.ble.mesh.foundation.parameter.AutoConnectParameters
import com.telink.ble.mesh.foundation.parameter.BindingParameters
import com.telink.ble.mesh.foundation.parameter.ProvisioningParameters
import com.telink.ble.mesh.foundation.parameter.ScanParameters
import com.telink.ble.mesh.util.Arrays
import com.telink.ble.mesh.util.MeshLogger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

class TelinkBleModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), TelinkBleEventEmitter, TelinkBleEventHandler {
  companion object {
    val app: TelinkBleApplication
      get() = TelinkBleApplication.getInstance()

    val meshInfo: MeshInfo
      get() = TelinkBleApplication.getInstance().getMeshInfo()!!

    var moduleInstance: TelinkBleModule? = null

    fun getInstance(): TelinkBleModule {
      return moduleInstance!!
    }
  }

  override fun getName(): String {
    return "TelinkBle"
  }

  override val eventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter
    get() = reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)

  private val delayHandler = Handler(Looper.getMainLooper())

  /**
   * found by bluetooth scan
   */
  private var devices: ArrayList<NetworkingDevice> = ArrayList()

  private var targetResettingAddress: Int? = null

  private val timePubSetTimeoutTask =
    Runnable { onTimePublishComplete(false, "time pub set timeout") }

  private var isPubSetting = false

  init {
    moduleInstance = this

    app.addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_BEGIN, this)
    app.addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_SUCCESS, this)
    app.addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_FAIL, this)
    app.addEventListener(BindingEvent.EVENT_TYPE_BIND_SUCCESS, this)
    app.addEventListener(BindingEvent.EVENT_TYPE_BIND_FAIL, this)
    app.addEventListener(ScanEvent.EVENT_TYPE_SCAN_TIMEOUT, this)
    app.addEventListener(ScanEvent.EVENT_TYPE_DEVICE_FOUND, this)
    app.addEventListener(ModelPublicationStatusMessage::class.java.name, this)
  }

  @ReactMethod
  fun autoConnect() {
    MeshLogger.log("main auto connect")
    MeshService.getInstance().autoConnect(AutoConnectParameters())
  }

  private fun getInt(byteArray: ByteArray): Int {
    val bb: ByteBuffer = ByteBuffer.allocate(2)
    bb.order(ByteOrder.LITTLE_ENDIAN)
    bb.put(byteArray[0])
    bb.put(byteArray[1])
    return bb.getInt(0)
  }

  @ReactMethod
  fun sendRawString(command: String) {
    val meshMessage = MeshMessage()
    val bytes = command.byteArray
    val destBytes = bytes.sliceArray(IntRange(8, 9))
    meshMessage.destinationAddress = getInt(bytes.sliceArray(IntRange(8, 9)))
    meshMessage.opcode = getInt(bytes.sliceArray(IntRange(10, 11)))
    meshMessage.sourceAddress = 0x0000
    meshMessage.appKeyIndex = meshInfo.defaultAppKeyIndex
    meshMessage.retryCnt = 0
    meshMessage.params = bytes.slice(IntRange(12, bytes.size - 1)).toByteArray()
    meshMessage.responseMax = 0
    MeshService.getInstance().sendMeshMessage(meshMessage)
  }

  @ReactMethod
  fun stopScanning() {
    MeshService.getInstance().stopScan()
    autoConnect()
  }

  @ReactMethod
  fun startAddingAllDevices() {
    devices = ArrayList()
    val parameters = ScanParameters.getDefault(false, false)
    parameters.setScanTimeout(10 * 1000L)
    MeshService.getInstance().startScan(parameters)
  }

  @ReactMethod
  fun getNodes(promise: Promise) {
    val nodes = meshInfo.nodes
    val result = WritableNativeArray()
    if (nodes != null) {
      for (node in nodes) {
        if (node.onOffDesc != "OFFLINE") {
          val nodeInfo = WritableNativeMap()
          nodeInfo.putString("uuid", node.deviceUUID.hexString)
          nodeInfo.putString("macAddress", node.macAddress)
          nodeInfo.putInt("meshAddress", node.meshAddress)
          nodeInfo.putString("deviceKey", "${node.deviceKey}")
          result.pushMap(nodeInfo)
        }
      }
      promise.resolve(result)
      return
    }
    promise.resolve(result)
  }

  @ReactMethod
  fun setStatus(meshAddress: Int, status: Boolean) {
    val rspMax: Int = if (!AppSettings.ONLINE_STATUS_ENABLE) meshInfo.onlineCountInAll else 0
    val appKeyIndex: Int = meshInfo.defaultAppKeyIndex
    val onOff = if (status) 1 else 0
    val onOffSetMessage = OnOffSetMessage.getSimple(
      meshAddress,
      appKeyIndex,
      onOff,
      !AppSettings.ONLINE_STATUS_ENABLE,
      rspMax,
    )
    MeshService.getInstance().sendMeshMessage(onOffSetMessage)
  }

  @ReactMethod
  fun setBrightness(meshAddress: Int, brightness: Int) {
    val rspMax: Int = if (!AppSettings.ONLINE_STATUS_ENABLE) meshInfo.onlineCountInAll else 0
    val node = meshInfo.nodes?.find {
      it.meshAddress == meshAddress
    }
    if (node !== null) {
      val lumEleInfo = node.lumEleInfo
      val message = LightnessSetMessage.getSimple(
        lumEleInfo!!.keyAt(0),
        meshInfo.defaultAppKeyIndex,
        UnitConvert.lum2lightness(brightness),
        !AppSettings.ONLINE_STATUS_ENABLE,
        rspMax
      )
      MeshService.getInstance().sendMeshMessage(message)
    }
  }

  @ReactMethod
  fun setTemperature(meshAddress: Int, temperature: Int) {
    val node = meshInfo.nodes?.find {
      it.meshAddress == meshAddress
    }
    if (node !== null) {
      val tempEleInfo = node.tempEleInfo
      val temperatureSetMessage = CtlTemperatureSetMessage.getSimple(
        tempEleInfo!!.keyAt(0),
        meshInfo.defaultAppKeyIndex,
        UnitConvert.temp100ToTemp(temperature),
        0,
        false,
        0
      )
      MeshService.getInstance().sendMeshMessage(temperatureSetMessage)
    }
  }

  @ReactMethod
  fun setHSL(meshAddress: Int, hsl: ReadableMap) {
    val node = meshInfo.nodes?.find {
      it.meshAddress == meshAddress
    }
    if (node !== null) {
      val hslElementAddress = node.getTargetEleAdr(MeshSigModel.SIG_MD_LIGHT_HSL_S.modelId)
      val hue = (hsl.getDouble("h") * 65535 / 360).roundToInt()
      val sat = UnitConvert.lum2lightness(hsl.getDouble("s").roundToInt())
      val lum = UnitConvert.lum2lightness(hsl.getDouble("l").roundToInt())
      val hslSetMessage = HslSetMessage.getSimple(
        hslElementAddress, meshInfo.defaultAppKeyIndex,
        lum,
        hue,
        sat,
        false,
        0
      )
      MeshService.getInstance().sendMeshMessage(hslSetMessage)
    }
  }

  @ReactMethod
  fun resetNode(meshAddress: Int) {
    // send reset message
    val cmdSent = MeshService.getInstance().sendMeshMessage(NodeResetMessage(meshAddress))
    val kickDirect = meshAddress == MeshService.getInstance().directConnectedNodeAddress
    targetResettingAddress = meshAddress
    if (!cmdSent || !kickDirect) {
      delayHandler.postDelayed(
        {
          onNodeResetFinished()
        },
        3 * 1000L
      )
    }
  }

  private fun onNodeResetFinished() {
    delayHandler.removeCallbacksAndMessages(null)
    MeshService.getInstance().removeDevice(targetResettingAddress!!)
    meshInfo.removeDeviceByMeshAddress(targetResettingAddress!!)
    meshInfo.saveOrUpdate(reactApplicationContext)
  }

  /**
   * @param state target state,
   * @return processing device
   */
  private fun getCurrentDevice(state: NetworkingState): NetworkingDevice? {
    for (device in devices) {
      if (device.state === state) {
        return device
      }
    }
    return null
  }

  override fun onProvisionStart(event: ProvisioningEvent) {
    val pvDevice: NetworkingDevice = getCurrentDevice(NetworkingState.PROVISIONING) ?: return
    pvDevice.addLog(NetworkingDevice.TAG_PROVISION, "begin")
  }

  override fun onProvisionFail(event: ProvisioningEvent) {
    val pvDevice: NetworkingDevice? = getCurrentDevice(NetworkingState.PROVISIONING)
    if (pvDevice == null) {
      MeshLogger.d("pv device not found when failed")
      return
    }
    pvDevice.state = NetworkingState.PROVISION_FAIL
    pvDevice.addLog(NetworkingDevice.TAG_PROVISION, event.desc)
  }

  override fun onProvisionSuccess(event: ProvisioningEvent) {
    val remote = event.provisioningDevice
    val pvDevice: NetworkingDevice = devices[devices.size - 1]
    pvDevice.state = NetworkingState.BINDING
    pvDevice.addLog(NetworkingDevice.TAG_PROVISION, "success")
    val nodeInfo: NodeInfo = pvDevice.nodeInfo
    val elementCnt = remote.deviceCapability.eleNum.toInt()
    nodeInfo.elementCnt = elementCnt
    nodeInfo.deviceKey = remote.deviceKey
    nodeInfo.netKeyIndexes.add(meshInfo.defaultNetKey.index)
    meshInfo.insertDevice(nodeInfo)
    meshInfo.increaseProvisionIndex(elementCnt)
    meshInfo.saveOrUpdate(reactApplicationContext)

    // check if private mode opened
    val privateMode = SharedPreferenceHelper.isPrivateMode(reactApplicationContext)

    // check if device support fast bind
    var defaultBound = false
    if (privateMode && remote.deviceUUID != null) {
      val device: PrivateDevice = PrivateDevice.filter(remote.deviceUUID)
      MeshLogger.d("private device")
      val cpsData: ByteArray = device.cpsData
      nodeInfo.compositionData = CompositionData.from(cpsData)
      defaultBound = true
    }
    nodeInfo.isDefaultBind = defaultBound
    pvDevice.addLog(NetworkingDevice.TAG_BIND, "action start")

    val appKeyIndex: Int = meshInfo.defaultAppKeyIndex
    val bindingDevice = BindingDevice(nodeInfo.meshAddress, nodeInfo.deviceUUID, appKeyIndex)
    bindingDevice.isDefaultBound = defaultBound
    bindingDevice.bearer = BindingBearer.GattOnly
    // bindingDevice.setDefaultBound(false);
    MeshService.getInstance().startBinding(BindingParameters(bindingDevice))
  }

  override fun onKeyBindFail(event: BindingEvent) {
    val deviceInList: NetworkingDevice = getCurrentDevice(NetworkingState.BINDING) ?: return
    deviceInList.state = NetworkingState.BIND_FAIL
    deviceInList.addLog(NetworkingDevice.TAG_BIND, "failed - " + event.desc)
    meshInfo.saveOrUpdate(reactApplicationContext)
  }

  override fun onKeyBindSuccess(event: BindingEvent) {
    val remote = event.bindingDevice
    val pvDevice: NetworkingDevice = devices[devices.size - 1]
    devices.removeAt(devices.size - 1)
    pvDevice.addLog(NetworkingDevice.TAG_BIND, "success")
    pvDevice.nodeInfo.bound = true
    // if is default bound, composition data has been valued ahead of binding action
    if (!remote.isDefaultBound) {
      pvDevice.nodeInfo.compositionData = remote.compositionData
    }
    if (setTimePublish(pvDevice)) {
      pvDevice.state = NetworkingState.TIME_PUB_SETTING
      pvDevice.addLog(NetworkingDevice.TAG_PUB_SET, "action start")
      isPubSetting = true
      MeshLogger.d("waiting for time publication status")
    } else {
      // no need to set time publish
      pvDevice.state = NetworkingState.BIND_SUCCESS
      startProvisionNextDevice()
    }

    val advertisingDevice = pvDevice.advertisingDevice
    val bleDevice = WritableNativeMap()
    val manufacturerData = advertisingDevice.scanRecord.hexString
    bleDevice.putString("manufacturerData", manufacturerData)
    bleDevice.putString("deviceType", manufacturerData.substring(147, 147 + 8))
    bleDevice.putString("uuid", pvDevice.nodeInfo.deviceUUID.hexString)
    bleDevice.putString("name", advertisingDevice.device.name)
    bleDevice.putString("macAddress", advertisingDevice.device.address)
    bleDevice.putInt("meshAddress", pvDevice.nodeInfo.meshAddress)
    bleDevice.putInt("rssi", advertisingDevice.rssi)
    sendEventWithName(TelinkBleEvent.EVENT_DEVICE_FOUND, bleDevice)
    meshInfo.saveOrUpdate(reactApplicationContext)
  }

  /**
   * set time publish after key bind success
   *
   * @param networkingDevice target
   * @return
   */
  private fun setTimePublish(networkingDevice: NetworkingDevice): Boolean {
    val modelId = MeshSigModel.SIG_MD_TIME_S.modelId
    val pubEleAdr = networkingDevice.nodeInfo.getTargetEleAdr(modelId)
    return if (pubEleAdr != -1) {
      val period = 30 * 1000L
      val pubAdr = MeshUtils.ADDRESS_BROADCAST
      val appKeyIndex: Int = meshInfo.defaultAppKeyIndex
      val modelPublication = ModelPublication.createDefault(
        pubEleAdr,
        pubAdr,
        appKeyIndex,
        period,
        modelId,
        true
      )
      val publicationSetMessage = ModelPublicationSetMessage(
        networkingDevice.nodeInfo.meshAddress,
        modelPublication
      )
      val result = MeshService.getInstance().sendMeshMessage(publicationSetMessage)
      if (result) {
        delayHandler.removeCallbacks(timePubSetTimeoutTask)
        delayHandler.postDelayed(timePubSetTimeoutTask, (5 * 1000).toLong())
      }
      result
    } else {
      false
    }
  }

  override fun startProvisionNextDevice() {
    if (devices.size > 0) {
      val waitingDevice: NetworkingDevice = devices[devices.size - 1]
      startProvision(waitingDevice)
      return;
    }
    sendEventWithName(TelinkBleEvent.EVENT_SCANNING_TIMEOUT, null)
    autoConnect()
  }

  private fun startProvision(processingDevice: NetworkingDevice) {
    val address: Int = meshInfo.provisionIndex
    MeshLogger.d("alloc address: $address")
    if (!MeshUtils.validUnicastAddress(address)) {
      return
    }
    val deviceUUID = processingDevice.nodeInfo.deviceUUID
    val provisioningDevice = ProvisioningDevice(
      processingDevice.bluetoothDevice,
      processingDevice.nodeInfo.deviceUUID,
      address
    )
    provisioningDevice.oobInfo = processingDevice.oobInfo
    processingDevice.state = NetworkingState.PROVISIONING
    processingDevice.addLog(
      NetworkingDevice.TAG_PROVISION,
      "action start -> 0x" + String.format("%04X", address)
    )
    processingDevice.nodeInfo.meshAddress = address
    // TODO: Send to JS
    // check if oob exists
    val oob: ByteArray? = meshInfo.getOOBByDeviceUUID(deviceUUID)
    provisioningDevice.authValue = oob
    run {
      val autoUseNoOOB = SharedPreferenceHelper.isNoOOBEnable(reactApplicationContext)
      provisioningDevice.isAutoUseNoOOB = autoUseNoOOB
    }
    val provisioningParameters = ProvisioningParameters(provisioningDevice)
    MeshLogger.d("provisioning device: $provisioningDevice")
    MeshService.getInstance().startProvisioning(provisioningParameters)
  }

  /**
   * only find in unprovisioned list
   *
   * @param deviceUUID deviceUUID in unprovisioned scan record
   */
  private fun deviceExists(deviceUUID: ByteArray): Boolean {
    for (device in devices) {
      if (device.state === NetworkingState.IDLE && Arrays.equals(
          deviceUUID,
          device.nodeInfo.deviceUUID
        )
      ) {
        return true
      }
    }
    return false
  }

  override fun onDeviceFound(advertisingDevice: AdvertisingDevice) {
    val serviceData = MeshUtils.getMeshServiceData(advertisingDevice.scanRecord, true)
    if (serviceData == null || serviceData.size < 16) {
      MeshLogger.log("serviceData error", MeshLogger.LEVEL_ERROR)
      return
    }
    val uuidLen = 16
    val deviceUUID = ByteArray(uuidLen)
    System.arraycopy(serviceData, 0, deviceUUID, 0, uuidLen)
    if (deviceExists(deviceUUID)) {
      MeshLogger.d("device exists")
      return
    }
    val nodeInfo = NodeInfo()
    nodeInfo.meshAddress = -1
    nodeInfo.deviceUUID = deviceUUID
    nodeInfo.macAddress = advertisingDevice.device.address
    val processingDevice = NetworkingDevice(nodeInfo)
    processingDevice.bluetoothDevice = advertisingDevice.device
    processingDevice.state = NetworkingState.IDLE
    processingDevice.addLog(NetworkingDevice.TAG_SCAN, "device found")
    processingDevice.advertisingDevice = advertisingDevice
    devices.add(processingDevice)
  }

  private fun onTimePublishComplete(success: Boolean, desc: String) {
    if (!isPubSetting) return
    MeshLogger.d("pub set complete: $success -- $desc")
    isPubSetting = false
    val pvDevice = getCurrentDevice(NetworkingState.TIME_PUB_SETTING)
    if (pvDevice == null) {
      MeshLogger.d("pv device not found pub set success")
      return
    }
    pvDevice.addLog(NetworkingDevice.TAG_PUB_SET, if (success) "success" else "failed : $desc")
    pvDevice.state =
      if (success) {
        NetworkingState.TIME_PUB_SET_SUCCESS
      } else {
        NetworkingState.TIME_PUB_SET_FAIL
      }
    pvDevice.addLog(NetworkingDevice.TAG_PUB_SET, desc)
    // TODO: Send to JS
    meshInfo.saveOrUpdate(reactApplicationContext)
    startProvisionNextDevice()
  }

  override fun onUnprovisionedDeviceScanningFinish() {
    sendEventWithName(TelinkBleEvent.EVENT_ANDROID_SCAN_FINISH, null)
    startProvisionNextDevice()
  }

  override fun onModelPublicationStatusMessage(event: Event<String?>?) {
    MeshLogger.d("pub setting status: $isPubSetting")
    if (!isPubSetting) {
      return
    }
    delayHandler.removeCallbacks(timePubSetTimeoutTask)
    val statusNotificationEvent = event as StatusNotificationEvent
    val statusMessage =
      statusNotificationEvent.notificationMessage.statusMessage as ModelPublicationStatusMessage
    if (statusMessage.status.toInt() == ConfigStatus.SUCCESS.code) {
      onTimePublishComplete(true, "time pub set success")
    } else {
      onTimePublishComplete(false, "time pub set status err: " + statusMessage.status)
      MeshLogger.log("publication err: " + statusMessage.status)
    }
  }
}
