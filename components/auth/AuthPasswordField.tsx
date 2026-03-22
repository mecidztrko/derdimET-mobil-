import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import type { Control, FieldPath, FieldValues } from 'react-hook-form';
import { Controller } from 'react-hook-form';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import Animated, { useAnimatedStyle, useSharedValue, withTiming } from 'react-native-reanimated';
import { AUTH, authShadows } from '@/constants/authTheme';

type Props<T extends FieldValues> = {
  control: Control<T>;
  name: FieldPath<T>;
  label: string;
  icon?: keyof typeof Ionicons.glyphMap;
  editable?: boolean;
};

export function AuthPasswordField<T extends FieldValues>({
  control,
  name,
  label,
  icon = 'lock-closed-outline',
  editable = true,
}: Props<T>) {
  const [focused, setFocused] = useState(false);
  const [hidden, setHidden] = useState(true);
  const focusAnim = useSharedValue(0);

  const ringStyle = useAnimatedStyle(() => {
    const focusedNow = focusAnim.value === 1;
    return {
      borderColor: focusedNow ? AUTH.accent : 'rgba(148, 163, 184, 0.35)',
      ...(focusedNow ? authShadows.inputFocus : {}),
    };
  });

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, onBlur, value }, fieldState: { error } }) => (
        <View style={styles.wrap}>
          <Text style={styles.label}>{label}</Text>
          <Animated.View style={[styles.fieldRow, ringStyle]}>
            <Ionicons name={icon} size={20} color={focused ? AUTH.accent : AUTH.textMuted} style={styles.iconL} />
            <TextInput
              style={styles.input}
              value={value as string}
              onChangeText={onChange}
              onFocus={() => {
                setFocused(true);
                focusAnim.value = withTiming(1, { duration: 180 });
              }}
              onBlur={() => {
                onBlur();
                setFocused(false);
                focusAnim.value = withTiming(0, { duration: 180 });
              }}
              placeholderTextColor={AUTH.textMuted}
              placeholder={label}
              secureTextEntry={hidden}
              autoCapitalize="none"
              autoCorrect={false}
              editable={editable}
            />
            <Pressable
              onPress={() => setHidden((h) => !h)}
              hitSlop={12}
              style={styles.iconR}
              accessibilityLabel={hidden ? 'Şifreyi göster' : 'Şifreyi gizle'}>
              <Ionicons name={hidden ? 'eye-off-outline' : 'eye-outline'} size={22} color={AUTH.textMuted} />
            </Pressable>
          </Animated.View>
          {error?.message ? <Text style={styles.error}>{error.message}</Text> : null}
        </View>
      )}
    />
  );
}

const styles = StyleSheet.create({
  wrap: {
    marginBottom: 16,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
    color: AUTH.text,
    marginBottom: 8,
    letterSpacing: 0.2,
  },
  fieldRow: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1.5,
    borderRadius: AUTH.radiusSm,
    backgroundColor: 'rgba(255,255,255,0.95)',
    paddingHorizontal: 4,
    minHeight: 52,
  },
  iconL: {
    marginLeft: 10,
  },
  iconR: {
    padding: 10,
  },
  input: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 8,
    fontSize: 16,
    color: AUTH.text,
  },
  error: {
    marginTop: 6,
    fontSize: 13,
    color: AUTH.error,
    fontWeight: '500',
  },
});
