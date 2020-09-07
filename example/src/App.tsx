/* eslint-disable react-native/no-inline-styles */
import * as React from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, {
  event,
  set,
  concat,
  block,
  debug,
} from 'react-native-reanimated';
import DeviceHeading from 'react-native-device-heading';

const AnimatedDeviceHeading = Animated.createAnimatedComponent(DeviceHeading);

export default function App() {
  const rotate = new Animated.Value(45);

  return (
    <View style={styles.container}>
      <AnimatedDeviceHeading
        style={{
          width: 100,
          height: 100,
          transform: [
            {
              rotate: concat(rotate, 'deg'),
            },
          ],
        }}
        onChange={event([
          {
            nativeEvent: {
              heading: (r: Animated.Value<number>) =>
                block([debug('rotate', r), set(rotate, r)]),
            },
          },
        ])}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
