import { useFocusEffect } from '@react-navigation/native';
import type { StackScreenProps } from '@react-navigation/stack';
import type { FC } from 'react';
import React from 'react';
import { FlatList, StyleSheet, Text, TouchableOpacity } from 'react-native';
import { Appbar, Checkbox } from 'react-native-paper';
import TelinkBle, { NodeInfo } from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import { globalState } from '../app/global-state';
import type { Scene } from '../models/scene';
import { DeviceControlScreen } from './DeviceControlScreen';

export interface SceneScreenProps extends StackScreenProps<any> {}

const SceneCreateScreen: FC<SceneScreenProps> = (props: SceneScreenProps) => {
  const { navigation } = props;

  const [nodes, setNodes] = React.useState<NodeInfo[]>([]);

  const [selectedNodes, setSelectedNodes] = React.useState<number[]>([]);

  useFocusEffect(
    React.useCallback(() => {
      TelinkBle.getNodes().then(setNodes);
    }, [])
  );

  return (
    <>
      <Appbar.Header>
        <Appbar.BackAction
          onPress={() => {
            navigation?.goBack();
          }}
        />
        <Appbar.Content title={SceneCreateScreen.displayName} />
        <Appbar.Action
          icon="account-arrow-right"
          onPress={async () => {
            const sceneId: number =
              Math.max(
                2,
                ...Object.values(globalState.scenes).map((scene) => scene.id!)
              ) + 2;
            const scene: Scene = {
              id: sceneId,
              nodes: selectedNodes.map(
                (id: number) => nodes.find((node) => node.unicastId === id)!
              ),
            };
            await globalState.addScene(scene);
            for (const nodeId of selectedNodes) {
              TelinkBle.setSceneForDevice(2, nodeId);
              await new Promise<void>((resolve) => {
                setTimeout(() => {
                  resolve();
                }, 500);
              });
            }
            navigation?.goBack();
          }}
        />
      </Appbar.Header>
      <FlatList
        style={styles.list}
        data={nodes}
        keyExtractor={(key: NodeInfo) => key.address}
        renderItem={({ item }) => {
          const checked = selectedNodes.includes(item.unicastId);
          return (
            <React.Fragment key={item.address}>
              <TouchableOpacity
                style={styles.node}
                onPress={() => {
                  navigation?.navigate(DeviceControlScreen.displayName!, {
                    node: item,
                  });
                }}
              >
                <Text>
                  {item.unicastId} - {item.address}
                </Text>
                <Checkbox
                  status={checked ? 'checked' : 'unchecked'}
                  onPress={() => {
                    if (checked) {
                      setSelectedNodes(
                        selectedNodes.filter((id) => id !== item.unicastId)
                      );
                      return;
                    }
                    setSelectedNodes([...selectedNodes, item.unicastId]);
                  }}
                />
              </TouchableOpacity>
            </React.Fragment>
          );
        }}
      />
    </>
  );
};

SceneCreateScreen.displayName = nameof(SceneCreateScreen);

export default SceneCreateScreen;

const styles = StyleSheet.create({
  list: {
    flex: 1,
    width: '100%',
    paddingTop: 8,
    paddingBottom: 8,
  },
  node: {
    padding: 16,
    marginTop: 8,
    marginBottom: 8,
    marginLeft: 16,
    marginRight: 16,
    display: 'flex',
    alignItems: 'center',
    borderRadius: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
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
  item: {
    borderWidth: 1,
  },
});
