/********************************************************************************************************
 * @file Scene.java
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

import java.io.Serializable
import java.util.*

/**
 * scene
 * Created by kee on 2018/10/8.
 */
class Scene : Serializable {
  /**
   * scene name
   */
  var name = "Telink-Scene"

  /**
   * scene id
   */
  var id = 0
  var states: MutableList<SceneState> = ArrayList()

  class SceneState : Serializable {
    /**
     * address
     * device unicast address(0x01 -- 0x7FFF) or group address (C000 - 0xFEFF)
     */
    var address = 0

    /**
     * on off value
     * -1 unknown
     */
    var onOff = 0

    /**
     * lum(lightness 0-100) value
     * -1 unknown
     */
    var lum = 0

    /**
     * temperature value
     * -1 unknown
     */
    var temp = 0

    constructor() {}
    constructor(address: Int) {
      this.address = address
      onOff = -1
      lum = -1
      temp = -1
    }
  }

  //    public List<Group> innerGroups = new ArrayList<>();
  //    public List<DeviceInfo> innerDevices = new ArrayList<>();
  fun saveFromDeviceInfo(deviceInfo: NodeInfo) {
    for (state in states) {
      if (state.address == deviceInfo.meshAddress) {
        state.onOff = deviceInfo.getOnOff()
        state.lum = deviceInfo.lum
        state.temp = deviceInfo.temp
        return
      }
    }
    val state = SceneState()
    state.address = deviceInfo.meshAddress
    state.onOff = deviceInfo.getOnOff()
    state.lum = deviceInfo.lum
    state.temp = deviceInfo.temp
    states.add(state)
  }

  fun removeByAddress(address: Int) {
    val iterator = states.iterator()
    while (iterator.hasNext()) {
      if (iterator.next().address == address) {
        iterator.remove()
        return
      }
    }
  }

  /*public void insertDevice(DeviceInfo deviceInfo) {
        for (DeviceInfo device : innerDevices) {
            if (device.meshAddress == deviceInfo.meshAddress) {
                device.macAddress = deviceInfo.macAddress;
                device.setOnOff(deviceInfo.getOnOff());
                device.lum = deviceInfo.lum;
                device.temp = deviceInfo.temp;
                return;
            }
        }
        DeviceInfo device = new DeviceInfo();
        device.meshAddress = deviceInfo.meshAddress;
        device.macAddress = deviceInfo.macAddress;
        device.setOnOff(deviceInfo.getOnOff());
        device.lum = deviceInfo.lum;
        device.temp = deviceInfo.temp;
        innerDevices.add(device);
    }*/
  /*public void deleteDevice(int address) {
        Iterator<DeviceInfo> iterator = innerDevices.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().meshAddress == address) {
                iterator.remove();
                return;
            }
        }
    }*/
  /*public boolean contains(Group group) {
        for (Group inner : innerGroups) {
            if (inner.address == group.address) {
                return true;
            }
        }
        return false;
    }*/
  operator fun contains(device: NodeInfo): Boolean {
    for (inner in states) {
      if (inner.address == device.meshAddress) {
        return true
      }
    }
    return false
  }
}
