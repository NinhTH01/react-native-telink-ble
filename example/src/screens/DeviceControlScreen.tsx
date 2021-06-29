import Slider from '@react-native-community/slider';
import type { StackScreenProps } from '@react-navigation/stack';
import { hex2Hsl, HSV, hsv2Hex } from 'colorsys';
import type { FC } from 'react';
import React from 'react';
import { StyleSheet } from 'react-native';
import { ColorPicker } from 'react-native-color-picker';
import TelinkBle, { NodeInfo } from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';

interface DeviceControlParams {
  node: NodeInfo;
}

interface DeviceControlProps
  extends StackScreenProps<Record<string, DeviceControlParams>> {}

export const DeviceControlScreen: FC<DeviceControlProps> = (
  props: DeviceControlProps
) => {
  const { node } = props.route.params;
  const [, setColor] = React.useState<string>('#FFFFFF');

  const handleChangeColor = React.useCallback(
    (c: HSV) => {
      const h = c.h;
      const s = c.s * 100;
      const v = c.v * 100;
      const hex = hsv2Hex({ h, s, v });
      const hsl = hex2Hsl(hex);
      setColor(hex);
      TelinkBle.setHsl(node.unicastId, hsl);
    },
    [node.unicastId]
  );

  return (
    <>
      <Slider
        style={styles.slider}
        minimumValue={0}
        maximumValue={100}
        minimumTrackTintColor="#FFFFFF"
        maximumTrackTintColor="#000000"
        onValueChange={(value: number) => {
          console.log(value);
          TelinkBle.setLuminance(node.unicastId, value);
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
          TelinkBle.setTemp(node.unicastId, value);
        }}
      />
      <ColorPicker onColorChange={handleChangeColor} style={styles.picker} />
    </>
  );
};

DeviceControlScreen.displayName = nameof(DeviceControlScreen);

const styles = StyleSheet.create({
  slider: {
    width: '100%',
    height: 40,
  },
  picker: {
    width: '100%',
    aspectRatio: 1,
  },
});
