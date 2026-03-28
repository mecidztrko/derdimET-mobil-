import React from 'react';
import { Pressable, ScrollView, StyleSheet, View } from 'react-native';
import { router } from 'expo-router';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import {
  ANIMAL_CATEGORY_LABELS,
  type AnimalCategoryFilter,
} from '@/constants/animalCategory';
import { useAnimalCategoryFilter } from '@/contexts/AnimalCategoryFilterContext';
import { useAuth } from '@/contexts/AuthContext';
import { USER_ROLE_LABELS } from '@/navigation/types';

const FILTER_OPTIONS: { key: AnimalCategoryFilter; label: string }[] = [
  { key: 'ALL', label: 'Tümü' },
  { key: 'KUCUKBAS', label: ANIMAL_CATEGORY_LABELS.KUCUKBAS },
  { key: 'BUYUKBAS', label: ANIMAL_CATEGORY_LABELS.BUYUKBAS },
];

export function ProfileScreen() {
  const { user, userRole, logout } = useAuth();
  const { filter, setFilter } = useAnimalCategoryFilter();

  const handleLogout = async () => {
    await logout();
    router.replace('/login');
  };

  return (
    <ThemedView style={styles.outer}>
      <ScrollView contentContainerStyle={styles.container} keyboardShouldPersistTaps="handled">
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

      <ThemedText type="subtitle" style={styles.filterHeading}>
        Hayvan ilanları filtresi
      </ThemedText>
      <ThemedText style={styles.filterHint}>
        Ana sayfadaki hayvan alış ilanları ve teklifleriniz listesinde yalnızca seçtiğiniz türü gösterir.
      </ThemedText>
      <View style={styles.chipRow}>
        {FILTER_OPTIONS.map((opt) => {
          const selected = filter === opt.key;
          return (
            <Pressable
              key={opt.key}
              onPress={() => setFilter(opt.key)}
              style={[styles.chip, selected && styles.chipSelected]}
              accessibilityRole="button"
              accessibilityState={{ selected }}>
              <ThemedText style={styles.chipText}>{opt.label}</ThemedText>
            </Pressable>
          );
        })}
      </View>

      <Pressable style={styles.button} onPress={handleLogout}>
        <ThemedText type="defaultSemiBold">Çıkış yap</ThemedText>
      </Pressable>
      </ScrollView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  outer: { flex: 1 },
  container: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    paddingBottom: 40,
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
    marginBottom: 28,
    opacity: 0.8,
    textAlign: 'center',
  },
  filterHeading: {
    alignSelf: 'stretch',
    textAlign: 'left',
    marginBottom: 6,
  },
  filterHint: {
    alignSelf: 'stretch',
    textAlign: 'left',
    opacity: 0.75,
    fontSize: 14,
    marginBottom: 12,
    lineHeight: 20,
  },
  chipRow: {
    alignSelf: 'stretch',
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 28,
    justifyContent: 'center',
  },
  chip: {
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: 'rgba(10, 126, 164, 0.35)',
    backgroundColor: 'transparent',
  },
  chipSelected: {
    backgroundColor: 'rgba(10, 126, 164, 0.25)',
    borderColor: 'rgba(10, 126, 164, 0.8)',
  },
  chipText: {
    fontSize: 14,
    fontWeight: '600',
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 12,
    backgroundColor: 'rgba(10, 126, 164, 0.2)',
  },
});
