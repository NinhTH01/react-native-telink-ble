import type { StackScreenProps } from '@react-navigation/stack';
import { hex2Hsl, hex2Hsv, hsv2Hex } from 'colorsys';
import type { FC } from 'react';
import { FlatList, StyleSheet, Switch, Text, View } from 'react-native';
import ExpandableSlider from 'react-native-expandable-slider';
import { RGBPicker } from 'react-native-light-color-picker';
import { Appbar, Button } from 'react-native-paper';
import TelinkBle, { BleEvent, NodeInfo } from 'react-native-telink-ble';
import React from 'reactn';
import nameof from 'ts-nameof.macro';
import { globalState } from '../app/global-state';
import GroupView from '../components/GroupView';
import { groups } from '../config/groups';
import { asyncStorageRepository } from '../repositories/async-storage-repository';

enum Tabs {
  CONTROL,
  GROUP,
  SETTING,
}

interface DeviceControlParams {
  node: NodeInfo;
}

interface DeviceControlProps
  extends StackScreenProps<Record<string, DeviceControlParams>> {}

export const DeviceControlScreen: FC<DeviceControlProps> = (
  props: DeviceControlProps
) => {
  const { node } = props.route.params;
  const { navigation } = props;

  const [color, setColor] = React.useState<string>('#FFFFFF');

  const [lum, setLum] = React.useState<number>(0);

  const [temp, setTemp] = React.useState<number>(0);

  const [onOff, setOnOff] = React.useState<boolean>(false);

  const [tab, setTab] = React.useState<Tabs>(Tabs.CONTROL);

  const handleChangeOnOff = React.useCallback(
    (newOnOff: boolean) => {
      TelinkBle.setOnOff(node.unicastId, newOnOff ? 1 : 0);
      setOnOff(newOnOff);
    },
    [node.unicastId]
  );

  const handleChangeColor = React.useCallback(
    (c: string) => {
      const { h, s, v } = hex2Hsv(c);
      const hex = hsv2Hex({ h, s, v });
      const hsl = hex2Hsl(hex);
      setColor(hex);
      TelinkBle.setHsl(node.unicastId, hsl);
    },
    [node.unicastId]
  );

  React.useEffect(() => {
    return TelinkBle.addEventListener(
      BleEvent.EVENT_RESET_NODE_SUCCESS,
      async () => {
        await asyncStorageRepository.removeGroups();
        navigation.goBack();
      }
    );
  }, [navigation]);

  return (
    <View style={styles.screen}>
      <Appbar.Header>
        <Appbar.BackAction
          onPress={() => {
            navigation?.goBack();
          }}
        />
        <Appbar.Content title={DeviceControlScreen.displayName} />
      </Appbar.Header>
      <View style={styles.actions}>
        <Button onPress={() => setTab(Tabs.CONTROL)}>CONTROL</Button>
        <Button onPress={() => setTab(Tabs.GROUP)}>GROUP</Button>
        <Button onPress={() => setTab(Tabs.SETTING)}>SETTING</Button>
      </View>

      {tab === Tabs.CONTROL && (
        <View style={styles.control}>
          <Switch value={onOff} onValueChange={handleChangeOnOff} />
          <Text>Luminance</Text>
          <ExpandableSlider
            style={styles.slider}
            min={0}
            max={100}
            value={lum}
            onSlideCompleted={(value: number) => {
              setLum(value);
              TelinkBle.setLuminance(node.unicastId, Math.floor(value));
            }}
          />
          <Text>CTL</Text>
          <ExpandableSlider
            style={styles.slider}
            min={0}
            max={100}
            value={temp}
            onSlideCompleted={(value: number) => {
              setTemp(value);
              TelinkBle.setTemp(node.unicastId, Math.floor(value));
            }}
          />
          <View style={styles.pickerContainer}>
            <RGBPicker value={color} onChangeComplete={handleChangeColor} />
          </View>
        </View>
      )}
      {tab === Tabs.GROUP && (
        <FlatList
          contentContainerStyle={styles.groupListContent}
          data={groups}
          keyExtractor={(group) => `${group.id}`}
          renderItem={({ item }) => (
            <GroupView
              key={item.id}
              group={item}
              defaultValue={globalState.groups[item.id]?.includes(
                node.unicastId
              )}
              onSwitch={async (value: boolean) => {
                if (value) {
                  TelinkBle.addDeviceToGroup(item.id, node.unicastId);
                  await globalState.addDeviceToGroup(node.unicastId, item.id);
                  return;
                }
                TelinkBle.removeDeviceFromGroup(item.id, node.unicastId);
                await globalState.removeDeviceFromGroup(
                  node.unicastId,
                  item.id
                );
              }}
            />
          )}
        />
      )}
      {tab === Tabs.SETTING && (
        <>
          <Button
            onPress={() => {
              TelinkBle.kickOut(node.unicastId);
            }}
          >
            Kick out
          </Button>
        </>
      )}
    </View>
  );
};

DeviceControlScreen.displayName = nameof(DeviceControlScreen);

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
  slider: {
    marginTop: 8,
    marginBottom: 8,
  },
  picker: {
    width: '100%',
    aspectRatio: 1,
  },
  btnReset: {
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 5,
    backgroundColor: 'red',
    padding: 16,
    position: 'absolute',
    bottom: 0,
    left: 0,
    width: '100%',
  },
  actions: {
    display: 'flex',
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 8,
    marginBottom: 8,
  },
  scrollView: {
    flex: 1,
  },
  kickOut: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
  },
  control: {
    paddingLeft: 16,
    paddingRight: 16,
    justifyContent: 'flex-start',
    alignContent: 'center',
    alignItems: 'center',
    flex: 1,
  },
  pickerContainer: {
    width: '100%',
  },
  groupListContent: {
    paddingTop: 8,
    paddingBottom: 8,
  },
});
