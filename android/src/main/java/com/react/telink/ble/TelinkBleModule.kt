package com.react.telink.ble

import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.react.telink.ble.helpers.toHexString
import com.react.telink.ble.model.NetworkingDevice
import com.react.telink.ble.model.NetworkingState
import com.react.telink.ble.model.NodeInfo
import com.telink.ble.mesh.core.MeshUtils
import com.telink.ble.mesh.entity.AdvertisingDevice
import com.telink.ble.mesh.foundation.*
import com.telink.ble.mesh.foundation.event.BindingEvent
import com.telink.ble.mesh.foundation.event.ProvisioningEvent
import com.telink.ble.mesh.foundation.event.ScanEvent
import com.telink.ble.mesh.foundation.parameter.ScanParameters
import com.telink.ble.mesh.util.MeshLogger

class TelinkBleModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), EventHandler, EventListener<String?> {
  companion object {
    private var instance: TelinkBleModule? = null

    fun getInstance(): TelinkBleModule? {
      return instance
    }
  }

  private fun onScanningTimeout(event: Event<String>?) {
    emitToJS(EVENT_SCANNING_TIMEOUT, null)
  }

  private val context: ReactApplicationContext = reactContext

  private var eventBus: EventBus<String>? = null;

  override fun getName(): String {
    return "TelinkBle"
  }

  fun setEventBus(eventBus: EventBus<String>) {
    this.eventBus = eventBus
  }

  private fun configureListeners() {
    addEventListener(ScanEvent.EVENT_TYPE_DEVICE_FOUND, this)
    addEventListener(ScanEvent.EVENT_TYPE_SCAN_TIMEOUT, this)
    addEventListener(ScanEvent.EVENT_TYPE_SCAN_LOCATION_WARNING, this)
    addEventListener(ScanEvent.EVENT_TYPE_SCAN_FAIL, this)
    addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_BEGIN, this)
    addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_FAIL, this)
    addEventListener(ProvisioningEvent.EVENT_TYPE_PROVISION_SUCCESS, this)
    addEventListener(BindingEvent.EVENT_TYPE_BIND_SUCCESS, this)
    addEventListener(BindingEvent.EVENT_TYPE_BIND_FAIL, this)
  }

  private fun configureEmitters() {
    context
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(EVENT_SCANNING_TIMEOUT, null)
  }

  /**
   * TODO: Implement onEventHandle
   */
  @ExperimentalUnsignedTypes
  @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  override fun onEventHandle(event: Event<String>?) {
    if (event?.type === ScanEvent.EVENT_TYPE_DEVICE_FOUND) {
      val device = (event as ScanEvent).advertisingDevice
      onDeviceFound(device)
    }

    if (event?.type === ScanEvent.EVENT_TYPE_SCAN_TIMEOUT) {
      onScanningTimeout(event)
    }
  }

  @ExperimentalUnsignedTypes
  @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private fun onDeviceFound(advertisingDevice: AdvertisingDevice) {
    val serviceData = MeshUtils.getMeshServiceData(advertisingDevice.scanRecord, true)
    if (serviceData == null || serviceData.size < 16) {
      MeshLogger.log("serviceData error", MeshLogger.LEVEL_ERROR)
      return
    }

    val uuidLen = 16
    val deviceUUID = ByteArray(uuidLen)
    System.arraycopy(serviceData, 0, deviceUUID, 0, uuidLen)

    val nodeInfo = NodeInfo()
    nodeInfo.meshAddress = -1
    nodeInfo.deviceUUID = deviceUUID
    nodeInfo.macAddress = advertisingDevice.device.address

    val processingDevice = NetworkingDevice(nodeInfo)
    processingDevice.bluetoothDevice = advertisingDevice.device
    processingDevice.state = NetworkingState.IDLE
    processingDevice.addLog(NetworkingDevice.TAG_SCAN, "device found")

    val device = WritableNativeMap()
    device.putInt("rssi", advertisingDevice.rssi)

    device.putString("scanRecord", advertisingDevice.scanRecord.toHexString())
    device.putString("uuid", deviceUUID.toHexString())
    device.putString("name", advertisingDevice.device.name)
    device.putString("address", advertisingDevice.device.address)
    device.putInt("type", advertisingDevice.device.type)
    device.putString("deviceClass", advertisingDevice.device.bluetoothClass.deviceClass.toString())
    device.putString(
      "majorDeviceClass",
      advertisingDevice.device.bluetoothClass.majorDeviceClass.toString()
    )
    device.putString("bluetoothClass", advertisingDevice.device.bluetoothClass.toString())
    device.putInt("bondState", advertisingDevice.device.bondState)
    if (advertisingDevice.device.uuids !== null) {
      val uuids = WritableNativeArray()
      for (item in advertisingDevice.device.uuids) {
        uuids.pushString(item.uuid.toString())
      }
      device.putArray("uuids", uuids)
    }
    emitToJS(EVENT_DEVICE_FOUND, device)
  }

  @ReactMethod
  fun initMeshNetwork(networkKey: String) {
    MeshService.getInstance().init(context, this)
    val meshConfiguration = MeshConfiguration()
    meshConfiguration.networkKey = networkKey.toByteArray()
    MeshService.getInstance().setupMeshNetwork(meshConfiguration)
    configureListeners()
    configureEmitters()
  }

  @ReactMethod
  fun createMeshNetwork(promise: Promise) {
    val networkKey: ByteArray = MeshUtils.generateRandom(16)
    promise.resolve(networkKey.toString())
  }

  @ReactMethod
  fun startScanning() {
    val scanParameters = ScanParameters()
    scanParameters.setScanTimeout(10 * 1000)
    MeshService.getInstance().startScan(scanParameters)
  }

  @ReactMethod
  fun stopScanning() {
    MeshService.getInstance().stopScan()
  }

  /**
   * add event listener
   *
   * @param eventType event type
   * @param listener  listener
   */
  private fun addEventListener(eventType: String?, listener: EventListener<String?>?) {
    eventBus?.addEventListener(eventType, listener)
  }

  private fun emitToJS(event: String, values: WritableNativeMap?) {
    context
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(event, values)
  }

  private fun emitToJS(event: String, values: WritableNativeArray) {
    context
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(event, values)
  }

  /**
   * remove listener
   */
  fun removeEventListener(listener: EventListener<String?>?) {
    eventBus?.removeEventListener(listener)
  }

  /**
   * remove target event from listener
   *
   * @param eventType type
   * @param listener  ls
   */
  fun removeEventListener(eventType: String?, listener: EventListener<String?>?) {
    eventBus?.removeEventListener(eventType, listener)
  }

  /**
   * remove all
   */
  fun removeEventListeners() {
    eventBus?.removeEventListeners()
  }

  /**
   * dispatch event from application
   */
  fun dispatchEvent(event: Event<String?>?) {
    eventBus?.dispatchEvent(event)
  }

  /**
   * TODO: Implement performed function
   */
  override fun performed(event: Event<String?>?) {
    println("performed: ${event?.type}")
  }
}
