package com.react.telink.ble

import com.telink.ble.mesh.core.message.generic.GenericMessage

class SceneControllerMessage(destinationAddress: Int, appKeyIndex: Int) :
  GenericMessage(destinationAddress, appKeyIndex) {

}
