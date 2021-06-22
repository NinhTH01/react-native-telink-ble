package com.react.telink.ble.model

class PublishModel(
  elementAddress: Int,
  modelId: Int,
  address: Int,
  period: Int,
  ttl: Int,
  credential: Int,
  transmit: Int
) {
  companion object {
    const val CREDENTIAL_FLAG_DEFAULT = 0b1;

    const val RFU_DEFAULT = 0x0C

    const val TTL_DEFAULT = 0xFF

    const val RETRANSMIT_COUNT_DEFAULT = 0x05

    const val RETRANSMIT_INTERVAL_STEP_DEFAULT = 0x02
  }

  var elementAddress = elementAddress

  private var modelId = modelId

  var address = address

  var period = period

  var ttl = ttl

  var credential = credential

  var transmit = transmit

  // higher 5 bit
  fun getTransmitInterval(): Int {
    return transmit and 0xFF shr 3
  }

  // lower 3 bit
  fun getTransmitCount(): Int {
    return transmit and 0xFF and 7
  }
}
