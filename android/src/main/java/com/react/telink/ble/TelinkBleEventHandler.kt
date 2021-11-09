package com.react.telink.ble

import com.telink.ble.mesh.core.message.config.ModelPublicationStatusMessage
import com.telink.ble.mesh.core.message.generic.OnOffStatusMessage
import com.telink.ble.mesh.core.message.lighting.CtlTemperatureStatusMessage
import com.telink.ble.mesh.core.message.lighting.HslStatusMessage
import com.telink.ble.mesh.core.message.lighting.LightnessStatusMessage
import com.telink.ble.mesh.entity.AdvertisingDevice
import com.telink.ble.mesh.foundation.Event
import com.telink.ble.mesh.foundation.EventListener
import com.telink.ble.mesh.foundation.event.BindingEvent
import com.telink.ble.mesh.foundation.event.ProvisioningEvent
import com.telink.ble.mesh.foundation.event.ScanEvent

interface TelinkBleEventHandler : EventListener<String?> {
  fun onProvisionStart(event: ProvisioningEvent)

  fun onProvisionFail(event: ProvisioningEvent)

  fun onProvisionSuccess(event: ProvisioningEvent)

  fun onKeyBindSuccess(event: BindingEvent)

  fun onKeyBindFail(event: BindingEvent)

  fun onModelPublicationStatusMessage(event: Event<String?>?)

  fun onDeviceFound(advertisingDevice: AdvertisingDevice)

  fun onUnprovisionedDeviceScanningFinish()

  fun startProvisionNextDevice()

  override fun performed(event: Event<String?>?) {
    when {
      event!!.type == ProvisioningEvent.EVENT_TYPE_PROVISION_BEGIN -> {
        onProvisionStart(event as ProvisioningEvent)
      }
      event.type == ProvisioningEvent.EVENT_TYPE_PROVISION_SUCCESS -> {
        onProvisionSuccess(event as ProvisioningEvent)
      }
      event.type == ScanEvent.EVENT_TYPE_SCAN_TIMEOUT -> {
        onUnprovisionedDeviceScanningFinish()
      }
      event.type == ProvisioningEvent.EVENT_TYPE_PROVISION_FAIL -> {
        onProvisionFail(event as ProvisioningEvent)
        startProvisionNextDevice()
      }
      event.type == BindingEvent.EVENT_TYPE_BIND_SUCCESS -> {
        onKeyBindSuccess(event as BindingEvent)
      }
      event.type == BindingEvent.EVENT_TYPE_BIND_FAIL -> {
        onKeyBindFail(event as BindingEvent)
        startProvisionNextDevice()
      }
      event.type == ScanEvent.EVENT_TYPE_DEVICE_FOUND -> {
        val device = (event as ScanEvent).advertisingDevice
        onDeviceFound(device)
      }
      event.type == ModelPublicationStatusMessage::class.java.name -> {
        onModelPublicationStatusMessage(event)
      }
      event.type === OnOffStatusMessage::javaClass.name -> {
        println(event)
      }
      event.type === LightnessStatusMessage::javaClass.name -> {
        println(event)
      }
      event.type === CtlTemperatureStatusMessage::javaClass.name -> {
        println(event)
      }
      event.type === HslStatusMessage::javaClass.name -> {
        println(event)
      }
    }
  }
}
