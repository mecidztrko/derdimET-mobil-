import { StyleSheet, View, type ViewProps } from 'react-native';
import { AUTH, authShadows } from '@/constants/authTheme';

export function AuthCard({ style, children, ...rest }: ViewProps) {
  return (
    <View style={[styles.card, authShadows.card, style]} {...rest}>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: AUTH.cardBg,
    borderRadius: AUTH.radiusLg,
    borderWidth: 1,
    borderColor: AUTH.border,
    padding: 22,
    overflow: 'hidden',
  },
});
