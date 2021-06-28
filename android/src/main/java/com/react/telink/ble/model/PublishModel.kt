package com.react.telink.ble.model

class PublishModel(
  var elementAddress: Int,
  private var modelId: Int,
  var address: Int,
  var period: Int,
  var ttl: Int,
  var credential: Int,
  var transmit: Int
) {
  companion object {
    const val CREDENTIAL_FLAG_DEFAULT = 0b1;

    const val RFU_DEFAULT = 0x0C

    const val TTL_DEFAULT = 0xFF

    const val RETRANSMIT_COUNT_DEFAULT = 0x05

    const val RETRANSMIT_INTERVAL_STEP_DEFAULT = 0x02
  }

  // higher 5 bit
  fun getTransmitInterval(): Int {
    return transmit and 0xFF shr 3
  }

  // lower 3 bit
  fun getTransmitCount(): Int {
    return transmit and 0xFF and 7
  }
}
