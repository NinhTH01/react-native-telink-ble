package com.react.telink.ble

import com.react.telink.ble.model.MeshInfo
import com.telink.ble.mesh.foundation.Event
import com.telink.ble.mesh.foundation.EventBus
import com.telink.ble.mesh.foundation.EventHandler
import com.telink.ble.mesh.foundation.EventListener
import com.telink.ble.mesh.foundation.event.MeshEvent
import com.telink.ble.mesh.foundation.event.NetworkInfoUpdateEvent
import com.telink.ble.mesh.foundation.event.OnlineStatusEvent
import com.telink.ble.mesh.foundation.event.StatusNotificationEvent

interface ReactMeshApplication : EventHandler {
  /**
   * Application event bus
   */
  abstract var mEventBus: EventBus<String?>?

  /**
   * Get mesh info from outside of this class
   */
  fun getMeshInfo(): MeshInfo

  /**
   * sequence-number or iv-index info update
   *
   * @param networkInfoUpdateEvent
   */
  fun onNetworkInfoUpdate(networkInfoUpdateEvent: NetworkInfoUpdateEvent)

  /**
   * device status notification
   *
   * @param statusNotificationEvent
   */
  fun onStatusNotificationEvent(statusNotificationEvent: StatusNotificationEvent?)

  /**
   * online status notification
   *
   * @param onlineStatusEvent
   */
  fun onOnlineStatusEvent(onlineStatusEvent: OnlineStatusEvent?)

  /**
   * mesh event
   *
   * @see MeshEvent.EVENT_TYPE_MESH_EMPTY
   *
   * @see MeshEvent.EVENT_TYPE_DISCONNECTED
   *
   * @see MeshEvent.EVENT_TYPE_MESH_RESET
   */
  fun onMeshEvent(meshEvent: MeshEvent?)

  /**
   * add event listener
   *
   * @param eventType event type
   * @param listener  listener
   */
  fun addEventListener(eventType: String?, listener: EventListener<String?>?)

  /**
   * remove listener
   *
   * @param listener Event listener
   */
  fun removeEventListener(listener: EventListener<String?>?)

  /**
   * remove target event from listener
   *
   * @param eventType type
   * @param listener  ls
   */
  fun removeEventListener(eventType: String?, listener: EventListener<String?>?)

  /**
   * remove all
   */
  fun removeEventListeners()

  /**
   * dispatch event from application
   *
   * @param event Event object
   */
  fun dispatchEvent(event: Event<String?>?)
}
