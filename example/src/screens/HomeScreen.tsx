import type { BottomTabScreenProps } from '@react-navigation/bottom-tabs';
import type { FC } from 'react';
import React from 'react';
import { FlatList, StyleSheet, View } from 'react-native';
import { Appbar, Button, Snackbar } from 'react-native-paper';
import TelinkBle, { BleEvent, NodeInfo } from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import NodeView from '../components/NodeView';
import { asyncStorageRepository } from '../repositories/async-storage-repository';
import { DeviceControlScreen } from './DeviceControlScreen';
import DeviceScanningScreen from './DeviceScanningScreen';

export const HomeScreen: FC<Partial<BottomTabScreenProps<any>>> = (
  props: Partial<BottomTabScreenProps<any>>
) => {
  const { navigation } = props;

  const [nodes, setNodes] = React.useState<NodeInfo[]>([]);

  const [visible, setVisible] = React.useState<boolean>(false);

  const [message, setMessage] = React.useState<string>('');

  React.useEffect(() => {
    return navigation?.addListener('focus', () => {
      TelinkBle.getNodes().then((newNodes) => {
        setNodes(newNodes);
      });
    });
  }, [navigation]);

  React.useEffect(() => {
    return navigation?.addListener('focus', () => {
      TelinkBle.autoConnect();
    });
  }, [navigation]);

  const handleNavigateToDeviceControl = React.useCallback(
    (node: NodeInfo) => () => {
      navigation?.navigate(DeviceControlScreen.displayName!, {
        node,
      });
    },
    [navigation]
  );

  React.useEffect(() => {
    const unsubscribeSuccess = TelinkBle.addEventListener(
      BleEvent.EVENT_MESH_CONNECT_SUCCESS,
      () => {
        setMessage('BLE network initialized');
        setVisible(true);
      }
    );

    const unsubscribeFailed = TelinkBle.addEventListener(
      BleEvent.EVENT_MESH_CONNECT_FAILED,
      () => {
        setMessage('Could not initialize mesh network');
        setVisible(true);
      }
    );

    return () => {
      unsubscribeFailed();
      unsubscribeSuccess();
    };
  }, []);

  const [loading, setLoading] = React.useState<boolean>(false);

  React.useEffect(() => {
    return TelinkBle.addEventListener(
      BleEvent.EVENT_REMOVE_SCENE_SUCCESS,
      async (id: number) => {
        const sceneAsync = await asyncStorageRepository.getScene();
        const albumMapper: Record<number, NodeInfo[]> = {};
        if (sceneAsync) {
          for (const key in sceneAsync) {
            if (key !== id.toString()) {
              albumMapper[key] = sceneAsync[key];
            }
          }
        }
        await asyncStorageRepository.saveScene(albumMapper);
      }
    );
  }, []);

  return (
    <>
      <Appbar.Header>
        <Appbar.Content title={HomeScreen.displayName} />
        <Appbar.Action
          icon="plus"
          onPress={() => {
            navigation?.navigate(DeviceScanningScreen.displayName!);
          }}
        />
      </Appbar.Header>
      <FlatList
        style={styles.outerContainer}
        contentContainerStyle={styles.innerContainer}
        data={nodes}
        keyExtractor={(nodeInfo: NodeInfo) => nodeInfo.address}
        renderItem={({ item, index }) => {
          return (
            <>
              <NodeView
                node={item}
                index={index}
                onPress={handleNavigateToDeviceControl(item)}
              />
            </>
          );
        }}
        ListHeaderComponent={
          <>
            <View style={styles.actions}>
              <Button
                onPress={() => {
                  TelinkBle.setAllOn();
                }}
              >
                All on
              </Button>
              <Button
                onPress={() => {
                  TelinkBle.setAllOff();
                }}
              >
                All off
              </Button>
            </View>
          </>
        }
        refreshing={loading}
        onRefresh={() => {
          setLoading(true);
          TelinkBle.getNodes().then((newNodes) => {
            setNodes(newNodes);
            setLoading(false);
          });
        }}
      />
      <Snackbar
        visible={visible}
        onDismiss={() => {
          setVisible(false);
        }}
        action={{
          label: 'Dismiss',
          onPress() {
            setVisible(false);
          },
        }}
      >
        {message}
      </Snackbar>
    </>
  );
};

HomeScreen.displayName = nameof(HomeScreen);

export default HomeScreen;

const styles = StyleSheet.create({
  outerContainer: {
    width: '100%',
    height: '100%',
    flex: 1,
    paddingTop: 8,
    paddingBottom: 8,
  },
  innerContainer: {
    width: '100%',
    minHeight: '100%',
  },
  actions: {
    flexDirection: 'row',
  },
});
