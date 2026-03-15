import React from 'react';
import { StyleSheet, TouchableOpacity } from 'react-native';
import { router } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { useAuth } from '@/contexts/AuthContext';
import { USER_ROLE_LABELS } from '@/navigation/types';

export function ProfileScreen() {
  const { userRole, logout } = useAuth();

  const handleLogout = () => {
    logout();
    router.replace('/');
  };

  return (
    <ThemedView style={styles.container}>
      {userRole && (
        <ThemedText style={styles.role}>Rol: {USER_ROLE_LABELS[userRole]}</ThemedText>
      )}
      <TouchableOpacity style={styles.button} onPress={handleLogout} activeOpacity={0.8}>
        <ThemedText type="defaultSemiBold">Çıkış Yap / Rol Değiştir</ThemedText>
      </TouchableOpacity>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  role: {
    marginBottom: 24,
    opacity: 0.8,
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 12,
    backgroundColor: 'rgba(10, 126, 164, 0.2)',
  },
});
