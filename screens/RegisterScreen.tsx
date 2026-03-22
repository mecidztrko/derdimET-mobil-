import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { router } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  AuthBackground,
  AuthCard,
  AuthGradientButton,
  AuthPasswordField,
  AuthTextField,
} from '@/components/auth';
import { AUTH } from '@/constants/authTheme';
import { useAuth } from '@/contexts/AuthContext';
import { getInitialRouteForRole } from '@/navigation/roleRouting';
import { registerSchema, type RegisterFormValues } from '@/schemas/authSchemas';

const ROLE_OPTIONS: { value: RegisterFormValues['role']; label: string }[] = [
  { value: 'MEAT_BUYER', label: 'Et alıcı' },
  { value: 'ANIMAL_SELLER', label: 'Hayvan satıcı' },
];

const TYPE_OPTIONS: { value: RegisterFormValues['accountType']; label: string }[] = [
  { value: 'INDIVIDUAL', label: 'Bireysel' },
  { value: 'BUSINESS', label: 'İşletme' },
];

export function RegisterScreen() {
  const insets = useSafeAreaInsets();
  const { registerAccount, isAuthenticated, userRole, bootstrapping } = useAuth();
  const [formError, setFormError] = useState<string | null>(null);

  const { control, handleSubmit, watch, setValue, formState } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
      role: 'MEAT_BUYER',
      accountType: 'INDIVIDUAL',
      companyName: '',
      taxNumber: '',
      address: '',
    },
    mode: 'onSubmit',
  });

  const accountType = watch('accountType');

  useEffect(() => {
    if (bootstrapping) return;
    if (isAuthenticated && userRole) {
      router.replace(getInitialRouteForRole(userRole));
    }
  }, [bootstrapping, isAuthenticated, userRole]);

  const onValid = async (data: RegisterFormValues) => {
    setFormError(null);
    try {
      await registerAccount({
        email: data.email,
        password: data.password,
        name: data.name,
        role: data.role,
        accountType: data.accountType,
        companyName: data.accountType === 'BUSINESS' ? data.companyName : undefined,
        taxNumber: data.accountType === 'BUSINESS' ? data.taxNumber : undefined,
        addressLine: data.accountType === 'BUSINESS' ? data.address?.trim() : undefined,
      });
      router.replace(getInitialRouteForRole(data.role));
    } catch (e) {
      setFormError(e instanceof Error ? e.message : 'Kayıt tamamlanamadı');
    }
  };

  if (bootstrapping) {
    return (
      <AuthBackground>
        <View style={styles.boot}>
          <ActivityIndicator color={AUTH.accent} size="large" />
        </View>
        <StatusBar style="dark" />
      </AuthBackground>
    );
  }

  return (
    <AuthBackground>
      <StatusBar style="dark" />
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 0 : 0}>
        <ScrollView
          contentContainerStyle={[
            styles.scroll,
            { paddingTop: insets.top + 12, paddingBottom: insets.bottom + 120 },
          ]}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}>
          <Pressable onPress={() => router.back()} hitSlop={12} style={styles.backRow}>
            <Text style={styles.backText}>‹ Geri</Text>
          </Pressable>

          <Text style={styles.title}>Hesap oluştur</Text>
          <Text style={styles.subtitle}>Bilgilerinizi girerek başlayın</Text>

          <AuthCard>
            <AuthTextField control={control} name="name" label="Ad soyad" icon="person-outline" editable={!formState.isSubmitting} />
            <AuthTextField
              control={control}
              name="email"
              label="E-posta"
              icon="mail-outline"
              keyboardType="email-address"
              autoCapitalize="none"
              editable={!formState.isSubmitting}
            />
            <AuthPasswordField control={control} name="password" label="Şifre" editable={!formState.isSubmitting} />
            <AuthPasswordField
              control={control}
              name="confirmPassword"
              label="Şifre tekrar"
              editable={!formState.isSubmitting}
            />

            <Text style={styles.sectionLabel}>Rol</Text>
            <Controller
              control={control}
              name="role"
              render={({ field: { value, onChange } }) => (
                <View style={styles.chipRow}>
                  {ROLE_OPTIONS.map((opt) => (
                    <TouchableOpacity
                      key={opt.value}
                      style={[styles.chip, value === opt.value && styles.chipOn]}
                      onPress={() => onChange(opt.value)}
                      activeOpacity={0.85}
                      disabled={formState.isSubmitting}>
                      <Text style={[styles.chipText, value === opt.value && styles.chipTextOn]}>{opt.label}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            />

            <Text style={styles.sectionLabel}>Hesap türü</Text>
            <Controller
              control={control}
              name="accountType"
              render={({ field: { value, onChange } }) => (
                <View style={styles.chipRow}>
                  {TYPE_OPTIONS.map((opt) => (
                    <TouchableOpacity
                      key={opt.value}
                      style={[styles.chip, value === opt.value && styles.chipOn]}
                      onPress={() => {
                        onChange(opt.value);
                        if (opt.value === 'INDIVIDUAL') {
                          setValue('companyName', '');
                          setValue('taxNumber', '');
                          setValue('address', '');
                        }
                      }}
                      activeOpacity={0.85}
                      disabled={formState.isSubmitting}>
                      <Text style={[styles.chipText, value === opt.value && styles.chipTextOn]}>{opt.label}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            />

            {accountType === 'BUSINESS' ? (
              <>
                <AuthTextField
                  control={control}
                  name="companyName"
                  label="Şirket adı"
                  icon="business-outline"
                  editable={!formState.isSubmitting}
                />
                <AuthTextField
                  control={control}
                  name="taxNumber"
                  label="Vergi numarası"
                  icon="document-text-outline"
                  editable={!formState.isSubmitting}
                />
                <AuthTextField
                  control={control}
                  name="address"
                  label="Adres"
                  icon="location-outline"
                  multiline
                  editable={!formState.isSubmitting}
                />
              </>
            ) : null}

            {formError ? <Text style={styles.formError}>{formError}</Text> : null}
          </AuthCard>
        </ScrollView>

        <View style={[styles.footer, { paddingBottom: insets.bottom + 14 }]}>
          <AuthGradientButton
            title="Kayıt ol"
            onPress={handleSubmit(onValid)}
            loading={formState.isSubmitting}
          />
          <Text style={styles.footerHint}>
            Kayıt olarak kullanım koşullarını kabul etmiş olursunuz.
          </Text>
        </View>
      </KeyboardAvoidingView>
    </AuthBackground>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  boot: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  formError: {
    color: AUTH.error,
    fontSize: 14,
    fontWeight: '500',
    textAlign: 'center',
    marginTop: 8,
  },
  scroll: {
    maxWidth: 440,
    width: '100%',
    alignSelf: 'center',
    paddingHorizontal: 20,
  },
  backRow: {
    alignSelf: 'flex-start',
    marginBottom: 12,
    paddingVertical: 4,
  },
  backText: {
    fontSize: 16,
    fontWeight: '600',
    color: AUTH.accent,
  },
  title: {
    fontSize: 26,
    fontWeight: '800',
    color: AUTH.text,
    letterSpacing: -0.3,
  },
  subtitle: {
    marginTop: 6,
    marginBottom: 18,
    fontSize: 14,
    color: AUTH.textMuted,
    fontWeight: '500',
  },
  sectionLabel: {
    fontSize: 13,
    fontWeight: '600',
    color: AUTH.text,
    marginBottom: 10,
    marginTop: 4,
  },
  chipRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 14,
  },
  chip: {
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: AUTH.radiusSm,
    borderWidth: 1.5,
    borderColor: 'rgba(148, 163, 184, 0.45)',
    backgroundColor: 'rgba(255,255,255,0.85)',
    marginRight: 10,
    marginBottom: 10,
  },
  chipOn: {
    borderColor: AUTH.accent,
    backgroundColor: 'rgba(37, 99, 235, 0.12)',
  },
  chipText: {
    fontSize: 14,
    fontWeight: '600',
    color: AUTH.text,
  },
  chipTextOn: {
    color: AUTH.accent,
  },
  footer: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    paddingHorizontal: 20,
    paddingTop: 10,
    backgroundColor: 'rgba(255,255,255,0.72)',
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: 'rgba(15, 23, 42, 0.08)',
  },
  footerHint: {
    marginTop: 10,
    textAlign: 'center',
    fontSize: 11,
    color: AUTH.textMuted,
    lineHeight: 16,
  },
});
