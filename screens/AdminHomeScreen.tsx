import React from 'react';
import { StyleSheet } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';

export function AdminHomeScreen() {
  return (
    <ThemedView style={styles.container}>
      <ThemedText type="title">Admin Paneli</ThemedText>
      <ThemedText style={styles.subtitle}>Yönetim ve raporlama ekranları burada olacak.</ThemedText>
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
  subtitle: {
    marginTop: 12,
    opacity: 0.8,
  },
});
