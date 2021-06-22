/********************************************************************************************************
 * @file     NodeStatusChangedEvent.java
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
package com.react.telink.ble.model

import android.os.Parcel
import android.os.Parcelable
import com.telink.ble.mesh.foundation.Event

/**
 * Created by kee on 2019/9/18.
 */
class NodeStatusChangedEvent : Event<String?>, Parcelable {
  var nodeInfo: NodeInfo? = null
    private set

  constructor(sender: Any?, type: String?, nodeInfo: NodeInfo?) : super(sender, type) {
    this.nodeInfo = nodeInfo
  }

  protected constructor(`in`: Parcel?) {}

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {}

  companion object {
    const val EVENT_TYPE_NODE_STATUS_CHANGED = "com.telink.ble.mesh.EVENT_TYPE_NODE_STATUS_CHANGED"
    @JvmField
    val CREATOR: Parcelable.Creator<NodeStatusChangedEvent?> =
      object : Parcelable.Creator<NodeStatusChangedEvent?> {
        override fun createFromParcel(`in`: Parcel): NodeStatusChangedEvent? {
          return NodeStatusChangedEvent(`in`)
        }

        override fun newArray(size: Int): Array<NodeStatusChangedEvent?> {
          return arrayOfNulls(size)
        }
      }
  }
}
