import React from 'react';
import {
  StyleSheet,
  Switch,
  Text,
  TouchableOpacity,
  TouchableOpacityProps,
} from 'react-native';
import type { NodeInfo } from 'react-native-telink-ble';
import TelinkBle from 'react-native-telink-ble';

interface NodeViewProps extends TouchableOpacityProps {
  node: NodeInfo;

  index: number;
}

export default function NodeView(props: NodeViewProps) {
  const { node, ...restProps } = props;

  const [onOff, setOnOff] = React.useState<boolean>(false);

  const handleOnOff = React.useCallback(
    (newOnOff: boolean) => {
      TelinkBle.setOnOff(node.unicastId, newOnOff ? 1 : 0);
      setOnOff(newOnOff);
    },
    [node.unicastId]
  );

  return (
    <TouchableOpacity style={[styles.container]} {...restProps}>
      <Text>{node.unicastId}</Text>
      <Switch value={onOff} onValueChange={handleOnOff} />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginLeft: 16,
    marginRight: 16,
    marginTop: 8,
    marginBottom: 8,
    paddingTop: 8,
    paddingBottom: 8,
    paddingLeft: 16,
    paddingRight: 16,
    borderRadius: 8,
    backgroundColor: '#FFF',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 3,
    },
    shadowOpacity: 0.27,
    shadowRadius: 4.65,
    elevation: 6,
    overflow: 'hidden',
  },
});
