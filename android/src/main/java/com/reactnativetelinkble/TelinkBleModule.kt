package com.reactnativetelinkble

import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.telink.ble.mesh.core.MeshUtils
import com.telink.ble.mesh.entity.AdvertisingDevice
import com.telink.ble.mesh.foundation.*
import com.telink.ble.mesh.foundation.event.ScanEvent
import com.telink.ble.mesh.foundation.parameter.ScanParameters

class TelinkBleModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), EventHandler {
  companion object {
    private var instance: TelinkBleModule? = null

    fun getInstance(): TelinkBleModule? {
      return instance
    }

    fun setEventBus(eventBus: EventBus<String>) {
      instance?.eventBus = eventBus
    }
  }

  private val context: ReactApplicationContext = reactContext

  private lateinit var eventBus: EventBus<String>;

  override fun getName(): String {
    return "TelinkBle"
  }

  private fun configureListeners() {

  }

  /**
   * TODO: Implement onEventHandle
   */
  @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  override fun onEventHandle(event: Event<String>?) {
    if (event?.type === ScanEvent.EVENT_TYPE_DEVICE_FOUND) {
      val device = (event as ScanEvent).advertisingDevice
      onDeviceFound(device)
    }
  }

  @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private fun onDeviceFound(device: AdvertisingDevice) {
    println("MAC:    ${device.device.address}")
    println("Name:   ${device.device.name}")
    println("Type:   ${device.device.type}")
    println("RSSI:   ${device.rssi}")
    println("Device class:         ${device.device.bluetoothClass.deviceClass}")
    println("Major Device class:   ${device.device.bluetoothClass.majorDeviceClass}")
    if (device.device.uuids !== null) {
      for ((i, uuid) in device.device.uuids.withIndex()) {
        println("UUID ${i}: ${uuid.uuid}")
      }
    }
    println("--------------- end of device info -----------------")
//    println("UUIDs:  ${device.device.uuids.joinToString(" - ")}")
    println("Record: ${device.scanRecord}")
  }

  @ReactMethod
  fun initMeshNetwork(networkKey: String) {
    MeshService.getInstance().init(context, this)
    val meshConfiguration = MeshConfiguration()
    meshConfiguration.networkKey = networkKey.toByteArray()
    MeshService.getInstance().setupMeshNetwork(meshConfiguration)
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

  /********************************************************************************
   * Event API
   */

  /**
   * add event listener
   *
   * @param eventType event type
   * @param listener  listener
   */
  fun addEventListener(eventType: String?, listener: EventListener<String?>?) {
    eventBus.addEventListener(eventType, listener)
  }

  /**
   * remove listener
   */
  fun removeEventListener(listener: EventListener<String?>?) {
    eventBus.removeEventListener(listener)
  }

  /**
   * remove target event from listener
   *
   * @param eventType type
   * @param listener  ls
   */
  fun removeEventListener(eventType: String?, listener: EventListener<String?>?) {
    eventBus.removeEventListener(eventType, listener)
  }

  /**
   * remove all
   */
  fun removeEventListeners() {
    eventBus.removeEventListeners()
  }

  /**
   * dispatch event from application
   */
  fun dispatchEvent(event: Event<String?>?) {
    eventBus.dispatchEvent(event)
  }
}
