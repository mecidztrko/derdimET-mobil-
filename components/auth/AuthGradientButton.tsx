import * as Haptics from 'expo-haptics';
import { LinearGradient } from 'expo-linear-gradient';
import { ActivityIndicator, Platform, Pressable, StyleSheet, Text } from 'react-native';
import Animated, { useAnimatedStyle, useSharedValue, withSpring } from 'react-native-reanimated';
import { AUTH, authShadows } from '@/constants/authTheme';

const AnimatedPressable = Animated.createAnimatedComponent(Pressable);

type Props = {
  title: string;
  onPress: () => void;
  loading?: boolean;
  disabled?: boolean;
};

export function AuthGradientButton({ title, onPress, loading, disabled }: Props) {
  const scale = useSharedValue(1);
  const animStyle = useAnimatedStyle(() => ({
    transform: [{ scale: scale.value }],
  }));

  const isDisabled = disabled || loading;

  return (
    <AnimatedPressable
      onPressIn={() => {
        scale.value = withSpring(0.98, { damping: 15, stiffness: 400 });
        if (Platform.OS === 'ios') {
          Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
        }
      }}
      onPressOut={() => {
        scale.value = withSpring(1, { damping: 12, stiffness: 320 });
      }}
      onPress={() => {
        if (!isDisabled) onPress();
      }}
      disabled={isDisabled}
      style={[styles.wrap, authShadows.button, animStyle]}>
      <LinearGradient
        colors={isDisabled ? ['#94A3B8', '#64748B'] : [AUTH.accentMid, AUTH.accent]}
        start={{ x: 0, y: 0.5 }}
        end={{ x: 1, y: 0.5 }}
        style={styles.gradient}>
        {loading ? (
          <ActivityIndicator color={AUTH.white} />
        ) : (
          <Text style={styles.label}>{title}</Text>
        )}
      </LinearGradient>
    </AnimatedPressable>
  );
}

const styles = StyleSheet.create({
  wrap: {
    borderRadius: AUTH.radiusMd,
    overflow: 'hidden',
  },
  gradient: {
    paddingVertical: 16,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 54,
  },
  label: {
    color: AUTH.white,
    fontSize: 16,
    fontWeight: '700',
    letterSpacing: 0.3,
  },
});
