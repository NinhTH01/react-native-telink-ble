/********************************************************************************************************
 * @file UnitConvert.java
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

import com.telink.ble.mesh.util.MeshLogger
import java.util.*
import kotlin.math.ceil

/**
 * Created by kee on 2017/11/28.
 */
object UnitConvert {
  /**
   * @param lum 0-100
   * @return -32768-32767
   */
  fun lum2level(lum: Int): Int {
    var l = lum
    if (l > 100) {
      l = 100
    }
    return -32768 + 65535 * l / 100
  }

  /**
   * @param level -32768-32767
   * @return lum 0-100
   */
  fun level2lum(level: Short): Int {
    val re = (level + 32768) * 100 / 65535
    MeshLogger.log("level2lum: $level re: $re")
    return re
  }

  /**
   * @param lum 0-100
   * @return 0-65535
   */
  fun lum2lightness(lum: Int): Int {
    return ceil(65535.toDouble() * lum / 100).toInt()
    //        return 65535 * lum / 100;
  }

  /**
   * @param lightness 0-65535
   * @return lum 0-100
   */
  fun lightness2lum(lightness: Int): Int {
    return lightness * 100 / 65535
  }

  /**
   * TEMP_MIN	 800
   * TEMP_MAX	 20000
   *
   * @param temp100 0-100
   * @return 800-2000
   */
  fun temp100ToTemp(temp100: Int): Int {
    var t100 = temp100
    if (t100 > 100) {
      t100 = 100
    }
    return 800 + (20000 - 800) * t100 / 100
  }

  /**
   * @param temp 800-2000
   * @return temp100 0-100
   */
  fun tempToTemp100(temp: Int): Int {
    if (temp < 800) return 0
    return if (temp > 20000) 100 else (temp - 800) * 100 / (20000 - 800)
  }// zone offset and daylight offset

  /**
   * get zone offset, 15min
   *
   * @return zoneOffSet
   */
  val zoneOffset: Int
    get() {
      val cal = Calendar.getInstance()
      // zone offset and daylight offset
      return (cal[Calendar.ZONE_OFFSET] + cal[Calendar.DST_OFFSET]) / 60 / 1000 / 15 + 64
    }
}
