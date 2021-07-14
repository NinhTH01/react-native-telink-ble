import type { BottomTabScreenProps } from '@react-navigation/bottom-tabs';
import type { FC } from 'react';
import { FlatList, StyleSheet } from 'react-native';
import { Appbar } from 'react-native-paper';
import React from 'reactn';
import nameof from 'ts-nameof.macro';
import type { GlobalState } from '../app/global-state';
import SceneView from '../components/SceneView';
import type { Scene } from '../models/scene';
import SceneCreateScreen from './SceneCreateScreen';

export interface SceneScreenProps extends BottomTabScreenProps<any> {}

const SceneScreen: FC<SceneScreenProps> = (props: SceneScreenProps) => {
  const { navigation } = props;

  const [scenes] = React.useGlobal<GlobalState, 'scenes'>('scenes');

  const handleToggleScene = React.useCallback(() => {
    navigation?.navigate(SceneCreateScreen.displayName!);
  }, [navigation]);

  return (
    <>
      <Appbar.Header>
        <Appbar.Content title={SceneScreen.displayName} />
        <Appbar.Action icon="plus" onPress={handleToggleScene} />
      </Appbar.Header>
      <FlatList
        style={styles.flatListView}
        data={Object.values(scenes)}
        keyExtractor={(key: Scene) => `${key.id!}`}
        renderItem={({ item, index }) => {
          return (
            <SceneView key={index} scene={item!} deviceList={item.nodes} />
          );
        }}
      />
    </>
  );
};

SceneScreen.displayName = nameof(SceneScreen);

export default SceneScreen;

const styles = StyleSheet.create({
  flatListView: {
    paddingTop: 8,
    paddingBottom: 8,
  },
  lineView: {},
  addIconView: {},
  icon: {},
});
