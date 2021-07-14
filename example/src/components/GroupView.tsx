import React from 'react';
import {
  StyleSheet,
  Switch,
  Text,
  TouchableOpacityProps,
  View,
} from 'react-native';
import type { Group } from '../models/group';

interface GroupViewProps extends TouchableOpacityProps {
  group: Group;

  defaultValue?: boolean;

  onSwitch?(value: boolean): any;
}

export default function GroupView(props: GroupViewProps) {
  const { group, onSwitch, defaultValue } = props;

  const [onOff, setOnOff] = React.useState<boolean>(defaultValue!);

  return (
    <View>
      <View style={styles.itemView}>
        <Text>{group.name}</Text>
        <Switch
          value={onOff}
          onValueChange={(value: boolean) => {
            setOnOff(value);
            if (typeof onSwitch === 'function') {
              onSwitch(value);
            }
          }}
        />
      </View>
    </View>
  );
}

GroupView.defaultProps = {
  defaultValue: false,
};

const styles = StyleSheet.create({
  itemView: {
    marginTop: 8,
    marginLeft: 16,
    marginRight: 16,
    marginBottom: 8,
    padding: 16,
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
});
