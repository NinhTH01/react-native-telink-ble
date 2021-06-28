/********************************************************************************************************
 * @file MeshInfo.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2010
 *
 * @par Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
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
package com.react.telink.ble.model

import android.content.Context
import android.util.SparseArray
import com.react.telink.ble.model.json.AddressRange
import com.telink.ble.mesh.core.MeshUtils
import com.telink.ble.mesh.foundation.MeshConfiguration
import com.telink.ble.mesh.util.Arrays
import com.telink.ble.mesh.util.FileSystem
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by kee on 2019/8/22.
 */
class MeshInfo : Serializable, Cloneable {
  /**
   * local provisioner UUID
   */
  var provisionerUUID: String? = null

  /**
   * unicast address range
   */
  var unicastRange: AddressRange? = null

  /**
   * nodes saved in mesh network
   */
  var nodes: MutableList<NodeInfo>? = ArrayList<NodeInfo>()

  /**
   * network key and network key index
   */
  lateinit var networkKey: ByteArray
  var netKeyIndex = 0

  /**
   * application key list
   */
  var appKeyList: MutableList<AppKey>? = null

  /**
   * ivIndex and sequence number are used in NetworkPDU
   *
   * @see NetworkLayerPDU.getSeq
   */
  var ivIndex = 0

  /**
   * provisioner sequence number
   */
  var sequenceNumber = 0

  /**
   * provisioner address
   */
  var localAddress = 0

  /**
   * unicast address prepared for node provisioning
   * increase by [element count] when provisioning success
   *
   * @see NodeInfo.elementCnt
   */
  var provisionIndex = 1

  /**
   * scenes saved in mesh
   */
  var scenes: MutableList<Scene> = ArrayList<Scene>()

  /**
   * groups
   */
  var groups: MutableList<GroupInfo> = ArrayList<GroupInfo>()

  /**
   * static-oob info
   */
  var oobPairs: List<OOBPair> = ArrayList<OOBPair>()
  val defaultAppKeyIndex: Int
    get() = if (appKeyList!!.size == 0) {
      0
    } else appKeyList!![0].index

  fun getDeviceByMeshAddress(meshAddress: Int): NodeInfo? {
    if (nodes == null) return null
    for (info in nodes!!) {
      if (info.meshAddress === meshAddress) return info
    }
    return null
  }

  /**
   * @param deviceUUID 16 bytes uuid
   */
  fun getDeviceByUUID(deviceUUID: ByteArray): NodeInfo? {
    for (info in nodes!!) {
      if (Arrays.equals(deviceUUID, info.deviceUUID)) return info
    }
    return null
  }

  fun insertDevice(deviceInfo: NodeInfo) {
    val local: NodeInfo? = getDeviceByUUID(deviceInfo.deviceUUID)
    if (local != null) {
      removeDeviceByUUID(deviceInfo.deviceUUID)
    }
    nodes!!.add(deviceInfo)
  }

  fun removeDeviceByMeshAddress(address: Int): Boolean {
    if (nodes == null || nodes!!.size == 0) return false
    for (scene in scenes) {
      scene.removeByAddress(address)
    }
    val iterator: MutableIterator<NodeInfo> = nodes!!.iterator()
    while (iterator.hasNext()) {
      val deviceInfo: NodeInfo = iterator.next()
      if (deviceInfo.meshAddress === address) {
        iterator.remove()
        return true
      }
    }
    return false
  }

  fun removeDeviceByUUID(deviceUUID: ByteArray?): Boolean {
    if (nodes == null || nodes!!.size == 0) return false
    val iterator: MutableIterator<NodeInfo> = nodes!!.iterator()
    while (iterator.hasNext()) {
      val deviceInfo: NodeInfo = iterator.next()
      if (Arrays.equals(deviceUUID, deviceInfo.deviceUUID)) {
        iterator.remove()
        return true
      }
    }
    return false
  }

  /**
   * get all online nodes
   */
  val onlineCountInAll: Int
    get() {
      if (nodes == null || nodes!!.size == 0) {
        return 0
      }
      var result = 0
      for (device in nodes!!) {
        if (device.getOnOff() !== -1) {
          result++
        }
      }
      return result
    }

  /**
   * get online nodes count in group
   *
   * @return
   */
  fun getOnlineCountInGroup(groupAddress: Int): Int {
    if (nodes == null || nodes!!.size == 0) {
      return 0
    }
    var result = 0
    for (device in nodes!!) {
      if (device.getOnOff() !== -1) {
        for (addr in device.subList) {
          if (addr == groupAddress) {
            result++
            break
          }
        }
      }
    }
    return result
  }

  fun saveScene(scene: Scene) {
    for (local in scenes) {
      if (local.id === scene.id) {
        local.states = scene.states
        return
      }
    }
    scenes.add(scene)
  }

  fun getSceneById(id: Int): Scene? {
    for (scene in scenes) {
      if (id == scene.id) {
        return scene
      }
    }
    return null
  }

  /**
   * 1-0xFFFF
   *
   * @return -1 invalid id
   */
  fun allocSceneId(): Int {
    if (scenes.size == 0) {
      return 1
    }
    val id: Int = scenes[scenes.size - 1].id
    return if (id == 0xFFFF) {
      -1
    } else id + 1
  }

  /**
   * get oob
   */
  fun getOOBByDeviceUUID(deviceUUID: ByteArray?): ByteArray? {
    for (pair in oobPairs) {
      if (Arrays.equals(pair.deviceUUID, deviceUUID)) {
        return pair.oob
      }
    }
    return null
  }

  fun saveOrUpdate(context: Context?) {
    FileSystem.writeAsObject(context, FILE_NAME, this)
  }

  class AppKey(var index: Int, var key: ByteArray) : Serializable

  override fun toString(): String {
    return "MeshInfo{" +
      "nodes=" + nodes!!.size +
      ", networkKey=" + Arrays.bytesToHexString(networkKey, "") +
      ", netKeyIndex=0x" + Integer.toHexString(netKeyIndex) +
      ", appKey=" + Arrays.bytesToHexString(appKeyList!![0].key, "") +
      ", appKeyIndex=0x" + Integer.toHexString(appKeyList!![0].index) +
      ", ivIndex=" + Integer.toHexString(ivIndex) +
      ", sequenceNumber=" + sequenceNumber +
      ", localAddress=" + localAddress +
      ", provisionIndex=" + provisionIndex +
      ", scenes=" + scenes.size +
      ", groups=" + groups.size +
      '}'
  }

  @Throws(CloneNotSupportedException::class)
  public override fun clone(): Any {
    return super.clone()
  }

  fun convertToConfiguration(): MeshConfiguration {
    val meshConfiguration = MeshConfiguration()
    meshConfiguration.deviceKeyMap = SparseArray<ByteArray>()
    if (nodes != null) {
      for (node in nodes!!) {
        meshConfiguration.deviceKeyMap.put(node.meshAddress, node.deviceKey)
      }
    }
    meshConfiguration.netKeyIndex = netKeyIndex
    meshConfiguration.networkKey = networkKey
    meshConfiguration.appKeyMap = SparseArray<ByteArray>()
    if (appKeyList != null) {
      for (appKey in appKeyList!!) {
        meshConfiguration.appKeyMap.put(appKey.index, appKey.key)
      }
    }
    meshConfiguration.ivIndex = ivIndex
    meshConfiguration.sequenceNumber = sequenceNumber
    meshConfiguration.localAddress = localAddress
    return meshConfiguration
  }

  companion object {
    /**
     * local storage file name , saved by serializi
     */
    const val FILE_NAME = "com.telink.ble.mesh.demo.STORAGE"
    fun createNewMesh(context: Context): MeshInfo {
      // 0x7FFF
      val defaultLocalAddress = 0x0001
      val meshInfo = MeshInfo()

      meshInfo.networkKey = MeshUtils.generateRandom(16)
      meshInfo.netKeyIndex = 0x00
      meshInfo.appKeyList = ArrayList<AppKey>() as MutableList<AppKey>
      meshInfo.appKeyList!!.add(AppKey(0x00, MeshUtils.generateRandom(16)))
      meshInfo.ivIndex = 0
      meshInfo.sequenceNumber = 0
      meshInfo.nodes = ArrayList<NodeInfo>()
      meshInfo.localAddress = defaultLocalAddress
      meshInfo.provisionIndex = defaultLocalAddress + 1 // 0x0002

      meshInfo.provisionerUUID = Arrays.bytesToHexString(MeshUtils.generateRandom(16))
      return meshInfo
    }
  }

  @JvmName("getDefaultAppKeyIndex1")
  fun getDefaultAppKeyIndex(): Int {
    return if (appKeyList!!.size == 0) {
      0
    } else appKeyList!![0].index
  }
}
