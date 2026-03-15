import React from 'react';
import { StyleSheet } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';

export function SellerHomeScreen() {
  return (
    <ThemedView style={styles.container}>
      <ThemedText type="title">Hayvan Satıcı</ThemedText>
      <ThemedText style={styles.subtitle}>
        Hayvan ilanları ve satış yönetimi burada olacak.
      </ThemedText>
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
