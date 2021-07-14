import type { FC } from 'react';
import React from 'react';
import { FlatList, StyleSheet } from 'react-native';
import { Appbar } from 'react-native-paper';
import TelinkBle from 'react-native-telink-ble';
import nameof from 'ts-nameof.macro';
import GroupView from '../components/GroupView';
import { groups } from '../config/groups';
import type { Group } from '../models/group';

const GroupScreen: FC = () => {
  return (
    <>
      <Appbar.Header>
        <Appbar.Content title={GroupScreen.displayName} />
      </Appbar.Header>
      <FlatList
        style={styles.flatListView}
        contentContainerStyle={styles.listContent}
        data={groups}
        keyExtractor={(group: Group) => group.id.toString()}
        renderItem={({ item, index }) => {
          return (
            <GroupView
              group={item}
              key={index}
              onSwitch={(value: boolean) => {
                TelinkBle.setOnOff(item.id, value ? 1 : 0);
              }}
            />
          );
        }}
      />
    </>
  );
};

GroupScreen.displayName = nameof(GroupScreen);

export default GroupScreen;

const styles = StyleSheet.create({
  flatListView: {},
  listContent: {
    paddingTop: 8,
    paddingBottom: 8,
  },
});
