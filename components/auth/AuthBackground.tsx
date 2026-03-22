import { ImageBackground, StyleSheet, View, type StyleProp, type ViewStyle } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';

type Props = {
  children: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  /** Splash için daha hafif üst katman */
  variant?: 'auth' | 'splash';
};

export function AuthBackground({ children, style, variant = 'auth' }: Props) {
  const gradientOpacity = variant === 'splash' ? [0.78, 0.88, 0.82] : [0.88, 0.92, 0.9];

  return (
    <ImageBackground
      source={require('@/assets/images/auth-background.png')}
      style={[styles.bg, style]}
      resizeMode="cover">
      <LinearGradient
        colors={[
          `rgba(255,255,255,${gradientOpacity[0]})`,
          `rgba(224,242,254,${gradientOpacity[1]})`,
          `rgba(239,246,255,${gradientOpacity[2]})`,
        ]}
        start={{ x: 0.5, y: 0 }}
        end={{ x: 0.5, y: 1 }}
        style={StyleSheet.absoluteFill}
      />
      <View style={styles.content}>{children}</View>
    </ImageBackground>
  );
}

const styles = StyleSheet.create({
  bg: {
    flex: 1,
    width: '100%',
    minHeight: '100%',
  },
  content: {
    flex: 1,
  },
});
