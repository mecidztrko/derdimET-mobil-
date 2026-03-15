import React from 'react';
import { StyleSheet, TouchableOpacity } from 'react-native';
import { router } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { useAuth } from '@/contexts/AuthContext';
import { USER_ROLE_LABELS, type UserRole } from '@/navigation/types';

const ROLES: UserRole[] = ['admin', 'hayvan_satici', 'et_alici'];

export function RoleSelectScreen() {
  const { setUserRole } = useAuth();

  const handleSelectRole = (role: UserRole) => {
    setUserRole(role);
    router.replace('/(tabs)');
  };

  return (
    <ThemedView style={styles.container}>
      <ThemedText type="title" style={styles.title}>
        DerdimET
      </ThemedText>
      <ThemedText style={styles.subtitle}>Giriş yapmak için rolünüzü seçin</ThemedText>
      {ROLES.map((role) => (
        <TouchableOpacity
          key={role}
          style={styles.button}
          onPress={() => handleSelectRole(role)}
          activeOpacity={0.8}
        >
          <ThemedText type="defaultSemiBold">{USER_ROLE_LABELS[role]}</ThemedText>
        </TouchableOpacity>
      ))}
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
  title: {
    marginBottom: 8,
  },
  subtitle: {
    marginBottom: 32,
    opacity: 0.8,
  },
  button: {
    width: '100%',
    maxWidth: 280,
    paddingVertical: 16,
    paddingHorizontal: 24,
    marginBottom: 12,
    borderRadius: 12,
    backgroundColor: 'rgba(10, 126, 164, 0.2)',
    alignItems: 'center',
  },
});
