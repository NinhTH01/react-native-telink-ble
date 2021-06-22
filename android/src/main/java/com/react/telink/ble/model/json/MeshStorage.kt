/********************************************************************************************************
 * @file     MeshStorage.java
 *
 * @brief    for TLSR chips
 *
 * @author   telink
 * @date     Sep. 30, 2010
 *
 * @par      Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
 * All rights reserved.
 *
 * The information contained herein is confidential and proprietary property of Telink
 * Semiconductor (Shanghai) Co., Ltd. and is available under the terms
 * of Commercial License Agreement between Telink Semiconductor (Shanghai)
 * Co., Ltd. and the licensee in separate contract or the terms described here-in.
 * This heading MUST NOT be removed from this file.
 *
 * Licensees are granted free, non-transferable use of the information in this
 * file under Mutual Non-Disclosure Agreement. NO WARRENTY of ANY KIND is provided.
 */
package com.react.telink.ble.model.json

import com.telink.ble.mesh.core.message.MeshMessage
import com.telink.ble.mesh.entity.Scheduler
import java.util.*

/**
 * Created by kee on 2018/9/10.
 *
 * change type of period in publish from integer to object
 * add HeartbeatPublication and HeartbeatSubscription
 */
class MeshStorage {
  internal interface Defaults {
    companion object {
      const val Schema = "http://json-schema.org/draft-04/schema#"
      const val Version = "1.0.0"
      const val Id =
        "http://www.bluetooth.com/specifications/assigned-numbers/mesh-profile/cdb-schema.json#"
      const val MeshName = "Telink-Sig-Mesh"
      const val IV_INDEX = 0
      const val KEY_INVALID = "00000000000000000000000000000000"
      const val ADDRESS_INVALID = "0000"
      const val LOCAL_DEVICE_KEY = "00112233445566778899AABBCCDDEEFF"
    }
  }

  var `$schema` = Defaults.Schema
  var id = Defaults.Id
  var version = Defaults.Version
  var meshName = Defaults.MeshName
  var meshUUID: String? = null
  var timestamp: String? = null
  var provisioners: MutableList<Provisioner?>? = null
  var netKeys: MutableList<NetworkKey?>? = null
  var appKeys: MutableList<ApplicationKey?>? = null

  /**
   * contains a local node (phone), its UUID is the same with provisioner uuid
   */
  var nodes: MutableList<Node?>? = null
  var groups: MutableList<Group?>? = null
  var scenes: MutableList<Scene?>? = null

  /**
   * custom
   */
  var ivIndex = String.format("%08X", Defaults.IV_INDEX)

  class Provisioner {
    var provisionerName: String? = null
    var UUID: String? = null
    var allocatedUnicastRange: List<AddressRange>? = null
    var allocatedGroupRange: List<AddressRange>? = null
    var allocatedSceneRange: List<SceneRange>? = null

    class AddressRange(var lowAddress: String, var highAddress: String)
    class SceneRange(var firstScene: String, var lastScene: String)
  }

  class NetworkKey {
    var name: String? = null

    // 0 -- 4095
    var index = 0

    // 0,1,2
    var phase = 0
    var key: String? = null
    var minSecurity: String? = null
    var oldKey = Defaults.KEY_INVALID
    var timestamp: String? = null
  }

  class ApplicationKey {
    var name: String? = null
    var index = 0
    var boundNetKey = 0
    var key: String? = null
    var oldKey = Defaults.KEY_INVALID
  }

  /**
   * only contains one netKey and appKey currently
   */
  class Node {
    // custom: not in doc
    //        public String macAddress;
    /**
     * sequence number
     * custom value
     * big endian string convert by mesh.sno
     * valued only when node is provisioner
     *
     * @see com.telink.ble.mesh.model.MeshInfo.sequenceNumber
     */
    //        public String sno;
    var UUID: String? = null
    var unicastAddress: String? = null
    var deviceKey: String? = null
    var security: String? = null
    var netKeys: MutableList<NodeKey?>? = null
    var configComplete = false
    var name: String? = null
    var cid: String? = null
    var pid: String? = null
    var vid: String? = null
    var crpl: String? = null
    var features: Features? = null
    var secureNetworkBeacon = true
    var defaultTTL = MeshMessage.DEFAULT_TTL
    var networkTransmit: Transmit? = null
    var relayRetransmit: Transmit? = null
    var appKeys: MutableList<NodeKey?>? = null
    var elements: MutableList<Element?>? = null
    var blacklisted = false

    // heartbeatPub
    var heartbeatPub: HeartbeatPublication? = null

    // heartbeatSub
    var heartbeatSub: List<HeartbeatSubscription>? = null

    // custom data for scheduler
    var schedulers: MutableList<NodeScheduler?>? = null
  }

  class NodeScheduler {
    var index: Byte = 0
    var year: Long = 0
    var month: Long = 0
    var day: Long = 0
    var hour: Long = 0
    var minute: Long = 0
    var second: Long = 0
    var week: Long = 0
    var action: Long = 0
    var transTime: Long = 0
    var sceneId = 0

    companion object {
      fun fromScheduler(scheduler: Scheduler): NodeScheduler {
        val nodeScheduler = NodeScheduler()
        nodeScheduler.index = scheduler.index
        val register = scheduler.register
        nodeScheduler.year = register.year
        nodeScheduler.month = register.month
        nodeScheduler.day = register.day
        nodeScheduler.hour = register.hour
        nodeScheduler.minute = register.minute
        nodeScheduler.second = register.second
        nodeScheduler.week = register.week
        nodeScheduler.action = register.action
        nodeScheduler.transTime = register.transTime
        nodeScheduler.sceneId = register.sceneId
        return nodeScheduler
      }
    }
  }

  class Features(var relay: Int, var proxy: Int, var friend: Int, var lowPower: Int)

  //Network transmit && Relay retransmit
  class Transmit(// 0--7
    var count: Int, // 10--120
    var interval: Int
  )

  // node network key && node application key
  class NodeKey(var index: Int, var updated: Boolean)
  class Element {
    var name: String? = null
    var index = 0
    var location: String? = null
    var models: MutableList<Model?>? = null
  }

  class Model {
    var modelId: String? = null
    var subscribe: MutableList<String?>? = ArrayList()
    var publish: Publish? = null
    var bind: MutableList<Int?>? = null
  }

  class Publish {
    var address: String? = null
    var index = 0
    var ttl = 0
    var period: PublishPeriod? = null
    var credentials = 0
    var retransmit: Transmit? = null
  }

  class PublishPeriod {
    /**
     * The numberOfStepa property contains an integer from 0 to 63 that represents the number of steps used
     * to calculate the publish period .
     */
    var numberOfSteps = 0

    /**
     * The resolution property contains an integer that represents the publish step resolution in milliseconds.
     * The allowed values are: 100, 1000, 10000, and 600000.
     */
    var resolution = 0
  }

  class HeartbeatPublication {
    var address: String? = null
    var period = 0
    var ttl = 0
    var index = 0
    var features: List<String>? = null
  }

  class HeartbeatSubscription {
    var source: String? = null
    var destination: String? = null
    var period = 0
  }

  class Group {
    var name: String? = null
    var address: String? = null
    var parentAddress = Defaults.ADDRESS_INVALID
  }

  class Scene {
    var name: String? = null
    var addresses: MutableList<String?>? = null
    var number: String? = null
  }
}
