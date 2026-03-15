import { useEffect } from 'react';
import { router } from 'expo-router';
import { RoleSelectScreen } from '@/screens/RoleSelectScreen';
import { useAuth } from '@/contexts/AuthContext';
import { ThemedView } from '@/components/themed-view';

/**
 * Başlangıç ekranı: Rol seçimi veya (giriş yapılmışsa) Tab ana sayfaya yönlendirme.
 */
export default function IndexScreen() {
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated) {
      router.replace('/(tabs)');
    }
  }, [isAuthenticated]);

  if (isAuthenticated) {
    return <ThemedView style={{ flex: 1 }} />;
  }

  return <RoleSelectScreen />;
}
