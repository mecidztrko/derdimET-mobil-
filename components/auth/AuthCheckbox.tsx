import { Ionicons } from '@expo/vector-icons';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { AUTH } from '@/constants/authTheme';

type Props = {
  checked: boolean;
  onToggle: () => void;
  label: string;
  disabled?: boolean;
};

export function AuthCheckbox({ checked, onToggle, label, disabled }: Props) {
  return (
    <Pressable
      onPress={onToggle}
      disabled={disabled}
      style={styles.row}
      hitSlop={8}
      accessibilityRole="checkbox"
      accessibilityState={{ checked }}>
      <View style={[styles.box, checked && styles.boxOn, disabled && styles.boxDisabled]}>
        {checked ? <Ionicons name="checkmark" size={16} color={AUTH.white} /> : null}
      </View>
      <Text style={styles.label}>{label}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  box: {
    width: 22,
    height: 22,
    marginRight: 10,
    borderRadius: 6,
    borderWidth: 2,
    borderColor: AUTH.border,
    backgroundColor: 'rgba(255,255,255,0.9)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  boxOn: {
    backgroundColor: AUTH.accent,
    borderColor: AUTH.accent,
  },
  boxDisabled: {
    opacity: 0.5,
  },
  label: {
    fontSize: 14,
    color: AUTH.text,
    fontWeight: '500',
  },
});
