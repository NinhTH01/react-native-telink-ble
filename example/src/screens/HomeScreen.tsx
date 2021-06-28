import Slider from '@react-native-community/slider';
import type { StackScreenProps } from '@react-navigation/stack';
import { hex2Hsl, HSV, hsv2Hex } from 'colorsys';
import DeviceScanningScreen from 'example/src/screens/DeviceScanningScreen';
import debounce from 'lodash/debounce';
import type { FC } from 'react';
import React from 'react';
import { FlatList, ScrollView, StyleSheet } from 'react-native';
import { ColorPicker } from 'react-native-color-picker';
import { Button, FAB } from 'react-native-paper';
import TelinkBle, { NodeInfo } from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import NodeView from '../components/NodeView';

export const HomeScreen: FC<Partial<StackScreenProps<any>>> = (
  props: Partial<StackScreenProps<any>>
) => {
  const { navigation } = props;

  const [nodes, setNodes] = React.useState<NodeInfo[]>([]);

  const [, setColor] = React.useState<string>('#FFFFFF');

  const handleChangeColor = React.useMemo(
    () =>
      debounce((c: HSV) => {
        const h = c.h;
        const s = c.s * 100;
        const v = c.v * 100;
        const hex = hsv2Hex({ h, s, v });
        const hsl = hex2Hsl(hex);
        console.log(hsl);
        TelinkBle.setHsl(2, hsl);
        setColor(hex);
      }, 300),
    []
  );

  React.useEffect(() => {
    return navigation?.addListener('focus', () => {
      TelinkBle.autoConnect();
    });
  }, [navigation]);

  return (
    <ScrollView style={styles.outerContainer}>
      <FAB
        style={styles.fab}
        small
        icon="plus"
        onPress={() => {
          navigation?.navigate(DeviceScanningScreen.displayName!);
        }}
      />
      <Button
        onPress={() => {
          TelinkBle.getNodes().then((newNodes: NodeInfo[]) => {
            setNodes(newNodes);
            console.log(newNodes);
          });
        }}
      >
        Get node
      </Button>
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
      <Slider
        style={styles.slider}
        minimumValue={0}
        maximumValue={100}
        minimumTrackTintColor="#FFFFFF"
        maximumTrackTintColor="#000000"
        onValueChange={(value: number) => {
          console.log(value);
          TelinkBle.setLuminance(2, value);
        }}
      />
      <Slider
        style={styles.slider}
        minimumValue={0}
        maximumValue={100}
        minimumTrackTintColor="#FFFFFF"
        maximumTrackTintColor="#000000"
        onValueChange={(value: number) => {
          console.log(value);
          TelinkBle.setTemp(2, value);
        }}
      />

      <ColorPicker onColorChange={handleChangeColor} style={styles.picker} />

      <FlatList
        data={nodes}
        keyExtractor={(nodeInfo: NodeInfo) => nodeInfo.address}
        renderItem={({ item, index }) => {
          return (
            <>
              <NodeView node={item} index={index} />
            </>
          );
        }}
      />
    </ScrollView>
  );
};

HomeScreen.displayName = nameof(HomeScreen);

export default HomeScreen;

const styles = StyleSheet.create({
  outerContainer: {
    width: '100%',
    height: '100%',
    flex: 1,
  },
  fab: {
    margin: 16,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1,
    position: 'absolute',
    right: 0,
    bottom: 0,
  },
  slider: { width: '100%', height: 40 },
  picker: {
    width: '100%',
    aspectRatio: 1,
  },
});
