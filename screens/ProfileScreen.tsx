import React from 'react';
import { StyleSheet, TouchableOpacity } from 'react-native';
import { router } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { useAuth } from '@/contexts/AuthContext';
import { USER_ROLE_LABELS } from '@/navigation/types';

export function ProfileScreen() {
  const { user, userRole, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    router.replace('/login');
  };

  return (
    <ThemedView style={styles.container}>
      {user ? (
        <>
          <ThemedText type="defaultSemiBold" style={styles.name}>
            {user.name}
          </ThemedText>
          <ThemedText style={styles.email}>{user.email}</ThemedText>
        </>
      ) : null}
      {userRole ? (
        <ThemedText style={styles.role}>Rol: {USER_ROLE_LABELS[userRole]}</ThemedText>
      ) : null}
      <TouchableOpacity style={styles.button} onPress={handleLogout} activeOpacity={0.8}>
        <ThemedText type="defaultSemiBold">Çıkış yap</ThemedText>
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
  name: {
    marginBottom: 4,
    textAlign: 'center',
  },
  email: {
    marginBottom: 16,
    opacity: 0.75,
    textAlign: 'center',
  },
  role: {
    marginBottom: 24,
    opacity: 0.8,
    textAlign: 'center',
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 12,
    backgroundColor: 'rgba(10, 126, 164, 0.2)',
  },
});
