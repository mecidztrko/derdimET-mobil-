import { useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import Animated, {
  Easing,
  useAnimatedStyle,
  useSharedValue,
  withDelay,
  withRepeat,
  withSequence,
  withTiming,
} from 'react-native-reanimated';
import { AUTH } from '@/constants/authTheme';

function Orb({
  size,
  top,
  left,
  right,
  delay,
  opacity,
}: {
  size: number;
  top: `${number}%` | number;
  left?: `${number}%` | number;
  right?: `${number}%` | number;
  delay: number;
  opacity: number;
}) {
  const y = useSharedValue(0);
  const x = useSharedValue(0);

  useEffect(() => {
    y.value = withDelay(
      delay,
      withRepeat(
        withSequence(
          withTiming(-14, { duration: 3200, easing: Easing.inOut(Easing.sin) }),
          withTiming(10, { duration: 3400, easing: Easing.inOut(Easing.sin) }),
        ),
        -1,
        true,
      ),
    );
    x.value = withDelay(
      delay + 200,
      withRepeat(
        withSequence(
          withTiming(8, { duration: 4000, easing: Easing.inOut(Easing.sin) }),
          withTiming(-6, { duration: 3800, easing: Easing.inOut(Easing.sin) }),
        ),
        -1,
        true,
      ),
    );
  }, [delay, x, y]);

  const style = useAnimatedStyle(() => ({
    transform: [{ translateX: x.value }, { translateY: y.value }],
    opacity,
  }));

  return (
    <Animated.View
      style={[
        styles.orb,
        { width: size, height: size, borderRadius: size / 2, top, left, right },
        style,
      ]}
    />
  );
}

export function FloatingOrbs() {
  return (
    <View style={StyleSheet.absoluteFill} pointerEvents="none">
      <Orb size={120} top="12%" left="6%" delay={0} opacity={0.35} />
      <Orb size={88} top="22%" right="8%" delay={400} opacity={0.28} />
      <Orb size={64} top="58%" left="12%" delay={200} opacity={0.22} />
      <Orb size={100} top="68%" right="4%" delay={600} opacity={0.25} />
    </View>
  );
}

const styles = StyleSheet.create({
  orb: {
    position: 'absolute',
    backgroundColor: AUTH.sky100,
    borderWidth: 1,
    borderColor: 'rgba(37, 99, 235, 0.12)',
  },
});
