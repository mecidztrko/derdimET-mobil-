import React, { useState } from 'react';
import {
  ActivityIndicator,
  Pressable,
  ScrollView,
  StyleSheet,
  TextInput,
  View,
} from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { ANIMAL_CATEGORY_LABELS, type AnimalCategory } from '@/constants/animalCategory';
import { Colors } from '@/constants/theme';
import { useColorScheme } from '@/hooks/use-color-scheme';
import { createAnimalPurchaseRequest } from '@/services/adminAnimalPurchaseApi';

export function AdminHomeScreen() {
  const colorScheme = useColorScheme();
  const palette = Colors[colorScheme ?? 'light'];

  const [title, setTitle] = useState('');
  const [animalCategory, setAnimalCategory] = useState<AnimalCategory | null>(null);
  const [quantity, setQuantity] = useState('');
  const [expectedWeight, setExpectedWeight] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit() {
    const t = title.trim();
    if (!t) {
      setError('Başlık zorunlu');
      return;
    }
    if (!animalCategory) {
      setError('Hayvan türü olarak küçükbaş veya büyükbaş seçin');
      return;
    }
    setSubmitting(true);
    setError(null);
    setMessage(null);
    try {
      const q = quantity.trim() === '' ? null : parseInt(quantity, 10);
      if (q !== null && (!Number.isFinite(q) || q < 1)) {
        setError('Adet geçerli bir pozitif tam sayı olmalı');
        setSubmitting(false);
        return;
      }
      let ew: number | null = null;
      if (expectedWeight.trim() !== '') {
        ew = Number(String(expectedWeight).replace(',', '.'));
        if (!Number.isFinite(ew) || ew <= 0) {
          setError('Beklenen ağırlık pozitif bir sayı olmalı');
          setSubmitting(false);
          return;
        }
      }
      await createAnimalPurchaseRequest({
        title: t,
        animalCategory,
        quantity: q,
        expectedWeight: ew,
        description: description.trim() || null,
      });
      setMessage('İlan oluşturuldu. Hayvan satıcıları görebilir.');
      setTitle('');
      setAnimalCategory(null);
      setQuantity('');
      setExpectedWeight('');
      setDescription('');
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Kayıt başarısız');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <ThemedView style={styles.root}>
      <ScrollView contentContainerStyle={styles.scroll}>
        <ThemedText type="title">Yönetici</ThemedText>
        <ThemedText style={styles.subtitle}>Hayvan alış ilanı oluştur (satıcılar görecek).</ThemedText>

        {error ? (
          <View style={styles.bannerErr}>
            <ThemedText style={styles.errText}>{error}</ThemedText>
          </View>
        ) : null}
        {message ? (
          <View style={styles.bannerOk}>
            <ThemedText style={styles.okText}>{message}</ThemedText>
          </View>
        ) : null}

        <ThemedText style={styles.label}>Başlık *</ThemedText>
        <TextInput
          value={title}
          onChangeText={setTitle}
          placeholder="örn. 5 adet besi danası"
          placeholderTextColor={palette.icon}
          style={[styles.input, { color: palette.text, borderColor: palette.icon + '55' }]}
        />

        <ThemedText style={styles.label}>Hayvan türü *</ThemedText>
        <View style={styles.categoryRow}>
          {(['KUCUKBAS', 'BUYUKBAS'] as const).map((key) => {
            const selected = animalCategory === key;
            return (
              <Pressable
                key={key}
                onPress={() => setAnimalCategory(key)}
                style={[
                  styles.categoryChip,
                  { borderColor: palette.icon + '55' },
                  selected && { backgroundColor: palette.tint + '33', borderColor: palette.tint },
                ]}>
                <ThemedText style={styles.categoryChipText}>{ANIMAL_CATEGORY_LABELS[key]}</ThemedText>
              </Pressable>
            );
          })}
        </View>

        <ThemedText style={styles.label}>Adet</ThemedText>
        <TextInput
          value={quantity}
          onChangeText={setQuantity}
          keyboardType="number-pad"
          placeholder="İsteğe bağlı"
          placeholderTextColor={palette.icon}
          style={[styles.input, { color: palette.text, borderColor: palette.icon + '55' }]}
        />

        <ThemedText style={styles.label}>Beklenen ağırlık (kg)</ThemedText>
        <TextInput
          value={expectedWeight}
          onChangeText={setExpectedWeight}
          keyboardType="decimal-pad"
          placeholder="İsteğe bağlı"
          placeholderTextColor={palette.icon}
          style={[styles.input, { color: palette.text, borderColor: palette.icon + '55' }]}
        />

        <ThemedText style={styles.label}>Açıklama</ThemedText>
        <TextInput
          value={description}
          onChangeText={setDescription}
          multiline
          placeholder="İsteğe bağlı detay"
          placeholderTextColor={palette.icon}
          style={[
            styles.input,
            styles.textArea,
            { color: palette.text, borderColor: palette.icon + '55' },
          ]}
        />

        <Pressable
          onPress={onSubmit}
          disabled={submitting}
          style={[styles.btn, { backgroundColor: palette.tint }]}>
          {submitting ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <ThemedText style={styles.btnText}>İlanı yayınla</ThemedText>
          )}
        </Pressable>
      </ScrollView>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  scroll: { padding: 20, paddingBottom: 40 },
  subtitle: { marginTop: 8, opacity: 0.85, marginBottom: 16 },
  label: { fontSize: 13, fontWeight: '600', marginTop: 12 },
  input: {
    borderWidth: 1,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    marginTop: 6,
  },
  textArea: { minHeight: 88, textAlignVertical: 'top' },
  categoryRow: {
    flexDirection: 'row',
    gap: 10,
    marginTop: 8,
    flexWrap: 'wrap',
  },
  categoryChip: {
    paddingVertical: 12,
    paddingHorizontal: 18,
    borderRadius: 12,
    borderWidth: 1,
  },
  categoryChipText: {
    fontSize: 15,
    fontWeight: '600',
  },
  btn: {
    marginTop: 24,
    borderRadius: 12,
    paddingVertical: 14,
    alignItems: 'center',
  },
  btnText: { color: '#fff', fontWeight: '700', fontSize: 16 },
  bannerErr: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: 'rgba(220, 38, 38, 0.12)',
    marginBottom: 8,
  },
  errText: { color: '#b91c1c' },
  bannerOk: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: 'rgba(22, 163, 74, 0.15)',
    marginBottom: 8,
  },
  okText: { color: '#15803d' },
});
