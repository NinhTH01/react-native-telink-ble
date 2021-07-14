import {
  StyleSheet,
  Text,
  TouchableOpacity,
  TouchableOpacityProps,
  View,
} from 'react-native';
import type { NodeInfo } from 'react-native-telink-ble';
import TelinkBle from 'react-native-telink-ble';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import React from 'reactn';
import { globalState } from '../app/global-state';
import type { Scene } from '../models/scene';

interface SceneViewProps extends TouchableOpacityProps {
  scene: Scene;

  deviceList?: NodeInfo[];
}

export default function SceneView(props: SceneViewProps) {
  const { scene, deviceList } = props;

  const handleOnOff = React.useCallback(() => {
    TelinkBle.triggerScene(scene.id!, scene.nodes!.length);
  }, [scene]);

  const handleDelete = React.useCallback(async () => {
    for (const node of deviceList! || []) {
      console.log(node);
      await TelinkBle.removeSceneFromDevice(scene.id!, node.unicastId);
      await new Promise<void>((resolve) => {
        setTimeout(() => {
          resolve();
        }, 500);
      });
    }
    await globalState.removeScene(scene);
  }, [deviceList, scene]);

  return (
    <View style={styles.container}>
      <Text>{scene.id}</Text>
      <View style={styles.flexRow}>
        <TouchableOpacity onPress={handleOnOff} style={styles.marR16px}>
          <MaterialIcons name="play-arrow" size={30} />
        </TouchableOpacity>
        <TouchableOpacity onPress={handleDelete}>
          <MaterialIcons name="delete" size={30} />
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginTop: 8,
    marginLeft: 16,
    marginRight: 16,
    marginBottom: 8,
    padding: 16,
    justifyContent: 'space-between',
    alignItems: 'center',
    flexDirection: 'row',
    borderRadius: 12,
    backgroundColor: '#FFF',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 3,
    },
    shadowOpacity: 0.27,
    shadowRadius: 4.65,
    elevation: 6,
  },
  flexRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  marR16px: {
    marginRight: 16,
  },
});
