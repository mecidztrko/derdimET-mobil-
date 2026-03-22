import { Ionicons } from '@expo/vector-icons';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import { router } from 'expo-router';
import { AuthBackground, FloatingOrbs } from '@/components/auth';
import { AUTH, authShadows } from '@/constants/authTheme';
import { useAuth } from '@/contexts/AuthContext';
import { getInitialRouteForRole } from '@/navigation/roleRouting';

const MIN_SPLASH_MS = 2600;

export function SplashScreen() {
  const { bootstrapping, isAuthenticated, userRole } = useAuth();
  const [minDone, setMinDone] = useState(false);

  useEffect(() => {
    const t = setTimeout(() => setMinDone(true), MIN_SPLASH_MS);
    return () => clearTimeout(t);
  }, []);

  useEffect(() => {
    if (!minDone || bootstrapping) return;
    if (isAuthenticated && userRole) {
      router.replace(getInitialRouteForRole(userRole));
    } else {
      router.replace('/login');
    }
  }, [minDone, bootstrapping, isAuthenticated, userRole]);

  return (
    <AuthBackground variant="splash" style={styles.fill}>
      <FloatingOrbs />
      <View style={styles.center}>
        <View style={[styles.logoWrap, authShadows.logo]}>
          <SplashLogoIcon />
        </View>
        <Text style={styles.appName}>DerdimET</Text>
        <Text style={styles.tagline}>Kesimhane otomasyonu</Text>
        <ActivityIndicator color={AUTH.accent} size="small" style={styles.loader} />
      </View>
    </AuthBackground>
  );
}

function SplashLogoIcon() {
  return (
    <View style={styles.logoInner}>
      <Ionicons name="nutrition" size={40} color={AUTH.accent} />
    </View>
  );
}

const styles = StyleSheet.create({
  fill: {
    flex: 1,
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 32,
  },
  logoWrap: {
    width: 96,
    height: 96,
    borderRadius: 24,
    backgroundColor: AUTH.white,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
  },
  logoInner: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  appName: {
    fontSize: 32,
    fontWeight: '800',
    color: AUTH.text,
    letterSpacing: -0.5,
  },
  tagline: {
    marginTop: 8,
    fontSize: 15,
    color: AUTH.textMuted,
    fontWeight: '500',
  },
  loader: {
    marginTop: 28,
  },
});
