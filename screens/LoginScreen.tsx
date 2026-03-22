import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { router } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  AuthBackground,
  AuthCard,
  AuthCheckbox,
  AuthGradientButton,
  AuthPasswordField,
  AuthTextField,
} from '@/components/auth';
import { AUTH } from '@/constants/authTheme';
import { useAuth } from '@/contexts/AuthContext';
import { getInitialRouteForRole } from '@/navigation/roleRouting';
import { loginSchema, type LoginFormValues } from '@/schemas/authSchemas';
import { getRememberedEmail, setRememberPreference } from '@/services/preferencesStorage';

export function LoginScreen() {
  const insets = useSafeAreaInsets();
  const { login, isAuthenticated, userRole, bootstrapping } = useAuth();
  const [rememberMe, setRememberMe] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [prefsLoaded, setPrefsLoaded] = useState(false);

  const { control, handleSubmit, reset } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', password: '' },
    mode: 'onSubmit',
  });

  useEffect(() => {
    let alive = true;
    (async () => {
      const { remember, email } = await getRememberedEmail();
      if (!alive) return;
      if (remember && email) {
        reset({ email, password: '' });
        setRememberMe(true);
      }
      setPrefsLoaded(true);
    })();
    return () => {
      alive = false;
    };
  }, [reset]);

  useEffect(() => {
    if (bootstrapping) return;
    if (isAuthenticated && userRole) {
      router.replace(getInitialRouteForRole(userRole));
    }
  }, [bootstrapping, isAuthenticated, userRole]);

  const onValid = async (data: LoginFormValues) => {
    setSubmitError(null);
    setLoading(true);
    try {
      const user = await login(data.email.trim(), data.password);
      await setRememberPreference(rememberMe, data.email.trim());
      router.replace(getInitialRouteForRole(user.role));
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : 'Giriş başarısız');
    } finally {
      setLoading(false);
    }
  };

  const forgotPassword = () => {
    Alert.alert(
      'Şifremi unuttum',
      'Şifre sıfırlama yakında e-posta ile eklenecek. Şimdilik destek ile iletişime geçebilirsiniz.',
      [{ text: 'Tamam' }],
    );
  };

  if (bootstrapping || !prefsLoaded) {
    return (
      <AuthBackground>
        <View style={styles.bootCenter}>
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
        keyboardVerticalOffset={Platform.OS === 'ios' ? 8 : 0}>
        <ScrollView
          contentContainerStyle={[
            styles.scroll,
            { paddingTop: insets.top + 16, paddingBottom: insets.bottom + 24 },
          ]}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}>
          <Text style={styles.title}>Hoş geldiniz</Text>
          <Text style={styles.subtitle}>Hesabınıza giriş yapın</Text>

          <AuthCard style={styles.card}>
            <AuthTextField
              control={control}
              name="email"
              label="E-posta"
              icon="mail-outline"
              keyboardType="email-address"
              autoCapitalize="none"
              editable={!loading}
            />
            <AuthPasswordField control={control} name="password" label="Şifre" editable={!loading} />

            <View style={styles.rowBetween}>
              <AuthCheckbox
                checked={rememberMe}
                onToggle={() => setRememberMe((v) => !v)}
                label="Beni hatırla"
                disabled={loading}
              />
              <Pressable onPress={forgotPassword} hitSlop={12} disabled={loading}>
                <Text style={styles.link}>Şifremi unuttum?</Text>
              </Pressable>
            </View>

            {submitError ? <Text style={styles.submitError}>{submitError}</Text> : null}

            <AuthGradientButton
              title="Giriş yap"
              onPress={handleSubmit(onValid)}
              loading={loading}
            />

            <Pressable
              style={styles.registerRow}
              onPress={() => router.push('/register')}
              disabled={loading}>
              <Text style={styles.registerMuted}>Hesabınız yok mu? </Text>
              <Text style={styles.registerBold}>Kayıt olun</Text>
            </Pressable>
          </AuthCard>
        </ScrollView>
      </KeyboardAvoidingView>
    </AuthBackground>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  bootCenter: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scroll: {
    flexGrow: 1,
    maxWidth: 440,
    width: '100%',
    alignSelf: 'center',
    paddingHorizontal: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: '800',
    color: AUTH.text,
    letterSpacing: -0.3,
  },
  subtitle: {
    marginTop: 8,
    marginBottom: 22,
    fontSize: 15,
    color: AUTH.textMuted,
    fontWeight: '500',
  },
  card: {
    marginTop: 4,
  },
  rowBetween: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 8,
    flexWrap: 'wrap',
  },
  link: {
    color: AUTH.accent,
    fontSize: 14,
    fontWeight: '600',
  },
  submitError: {
    color: AUTH.error,
    textAlign: 'center',
    marginBottom: 12,
    fontSize: 14,
    fontWeight: '500',
  },
  registerRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 22,
    flexWrap: 'wrap',
  },
  registerMuted: {
    fontSize: 14,
    color: AUTH.textMuted,
  },
  registerBold: {
    fontSize: 14,
    fontWeight: '700',
    color: AUTH.accent,
  },
});
