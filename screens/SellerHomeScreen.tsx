import React, { useCallback, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  Modal,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  TextInput,
  View,
} from 'react-native';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import {
  ANIMAL_CATEGORY_LABELS,
  labelForAnimalCategory,
  matchesAnimalCategoryFilter,
} from '@/constants/animalCategory';
import { Colors } from '@/constants/theme';
import { useAnimalCategoryFilter } from '@/contexts/AnimalCategoryFilterContext';
import { useColorScheme } from '@/hooks/use-color-scheme';
import {
  createAnimalOffer,
  fetchMyAnimalOffers,
  fetchOpenAnimalPurchaseRequests,
} from '@/services/sellerMarketApi';
import type { AnimalPurchaseRequestDto, SellerAnimalOfferItemDto } from '@/types/sellerMarket';

const OFFER_STATUS_LABEL: Record<string, string> = {
  PENDING: 'Beklemede',
  ACCEPTED: 'Kabul',
  REJECTED: 'Red',
};

function formatDate(iso: string): string {
  try {
    return new Date(iso).toLocaleString('tr-TR');
  } catch {
    return iso;
  }
}

export function SellerHomeScreen() {
  const colorScheme = useColorScheme();
  const palette = Colors[colorScheme ?? 'light'];
  const { filter: categoryFilter } = useAnimalCategoryFilter();
  const [requests, setRequests] = useState<AnimalPurchaseRequestDto[]>([]);
  const [offers, setOffers] = useState<SellerAnimalOfferItemDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [offerModalVisible, setOfferModalVisible] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState<AnimalPurchaseRequestDto | null>(null);
  const [pricePerKg, setPricePerKg] = useState('');
  const [animalCount, setAnimalCount] = useState('');
  const [note, setNote] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(async () => {
    setError(null);
    try {
      const [reqList, offerList] = await Promise.all([
        fetchOpenAnimalPurchaseRequests(),
        fetchMyAnimalOffers(),
      ]);
      setRequests(reqList);
      setOffers(offerList);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Veri yüklenemedi');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  React.useEffect(() => {
    load();
  }, [load]);

  const visibleRequests = useMemo(
    () => requests.filter((r) => matchesAnimalCategoryFilter(r.animalCategory, categoryFilter)),
    [requests, categoryFilter],
  );
  const visibleOffers = useMemo(
    () => offers.filter((o) => matchesAnimalCategoryFilter(o.request.animalCategory, categoryFilter)),
    [offers, categoryFilter],
  );

  const onRefresh = () => {
    setRefreshing(true);
    load();
  };

  const openOfferModal = (r: AnimalPurchaseRequestDto) => {
    setSelectedRequest(r);
    setPricePerKg('');
    setAnimalCount('');
    setNote('');
    setOfferModalVisible(true);
  };

  const submitOffer = async () => {
    if (!selectedRequest) return;
    const price = Number(String(pricePerKg).replace(',', '.'));
    if (!Number.isFinite(price) || price <= 0) {
      setError('Geçerli bir kg fiyatı girin');
      return;
    }
    let count: number | undefined;
    if (animalCount.trim() !== '') {
      count = parseInt(animalCount, 10);
      if (!Number.isFinite(count) || count < 1) {
        setError('Hayvan adedi 1 veya daha büyük olmalı');
        return;
      }
    }
    setSubmitting(true);
    setError(null);
    try {
      await createAnimalOffer(selectedRequest.id, {
        pricePerKg: price,
        animalCount: count ?? null,
        note: note.trim() || null,
      });
      setOfferModalVisible(false);
      setSelectedRequest(null);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Teklif gönderilemedi');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <ThemedView style={styles.centered}>
        <ActivityIndicator size="large" color={palette.tint} />
        <ThemedText style={styles.muted}>Yükleniyor…</ThemedText>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.root}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
        <ThemedText type="title">Hayvan alış ilanları</ThemedText>
        <ThemedText style={styles.subtitle}>
          Yönetici tarafından açılan taleplere teklif verebilirsiniz. Liste türünü Profil sekmesinden
          filtreleyebilirsiniz (şu an:{' '}
          {categoryFilter === 'ALL'
            ? 'Tümü'
            : ANIMAL_CATEGORY_LABELS[categoryFilter]}
          ).
        </ThemedText>

        {error ? (
          <View style={styles.errorBox}>
            <ThemedText style={styles.errorText}>{error}</ThemedText>
            <Pressable onPress={() => setError(null)} style={styles.dismissErr}>
              <ThemedText style={styles.dismissErrText}>Kapat</ThemedText>
            </Pressable>
          </View>
        ) : null}

        <ThemedText type="subtitle" style={styles.sectionTitle}>
          Açık ilanlar
        </ThemedText>
        {visibleRequests.length === 0 ? (
          <ThemedText style={styles.muted}>
            {requests.length === 0
              ? 'Şu an açık ilan yok.'
              : 'Seçtiğiniz filtreye uygun açık ilan yok. Profilden filtreyi değiştirebilirsiniz.'}
          </ThemedText>
        ) : (
          visibleRequests.map((r) => (
            <View key={r.id} style={[styles.card, { borderColor: palette.icon + '44' }]}>
              <ThemedText type="defaultSemiBold">{r.title}</ThemedText>
              <ThemedText style={styles.small}>Tür: {labelForAnimalCategory(r.animalCategory)}</ThemedText>
              {r.quantity != null ? <ThemedText style={styles.small}>Adet: {r.quantity}</ThemedText> : null}
              {r.expectedWeight != null ? (
                <ThemedText style={styles.small}>Beklenen ağırlık: {r.expectedWeight} kg</ThemedText>
              ) : null}
              {r.description ? <ThemedText style={styles.desc}>{r.description}</ThemedText> : null}
              <ThemedText style={styles.smallMuted}>{formatDate(r.createdAt)}</ThemedText>
              <Pressable
                onPress={() => openOfferModal(r)}
                style={[styles.primaryBtn, { backgroundColor: palette.tint }]}>
                <ThemedText style={styles.primaryBtnText}>Teklif ver</ThemedText>
              </Pressable>
            </View>
          ))
        )}

        <ThemedText type="subtitle" style={[styles.sectionTitle, styles.sectionSpacer]}>
          Verdiğim teklifler
        </ThemedText>
        {visibleOffers.length === 0 ? (
          <ThemedText style={styles.muted}>
            {offers.length === 0
              ? 'Henüz teklif yok.'
              : 'Seçtiğiniz filtreye uygun teklif yok.'}
          </ThemedText>
        ) : (
          visibleOffers.map((o) => (
            <View key={o.offerId} style={[styles.card, { borderColor: palette.icon + '44' }]}>
              <ThemedText type="defaultSemiBold">{o.request.title}</ThemedText>
              <ThemedText style={styles.smallMuted}>
                {labelForAnimalCategory(o.request.animalCategory)}
              </ThemedText>
              <ThemedText style={styles.small}>
                {o.pricePerKg} ₺/kg
                {o.animalCount != null ? ` · ${o.animalCount} hayvan` : ''}
              </ThemedText>
              <ThemedText style={styles.small}>Durum: {OFFER_STATUS_LABEL[o.status] ?? o.status}</ThemedText>
              {o.note ? <ThemedText style={styles.desc}>{o.note}</ThemedText> : null}
              <ThemedText style={styles.smallMuted}>{formatDate(o.createdAt)}</ThemedText>
            </View>
          ))
        )}
      </ScrollView>

      <Modal visible={offerModalVisible} animationType="slide" transparent>
        <View style={styles.modalBackdrop}>
          <ThemedView style={styles.modalCard}>
            <ThemedText type="subtitle">Teklif</ThemedText>
            {selectedRequest ? (
              <>
                <ThemedText style={styles.modalHint}>{selectedRequest.title}</ThemedText>
                <ThemedText style={styles.modalHint}>
                  Tür: {labelForAnimalCategory(selectedRequest.animalCategory)}
                </ThemedText>
              </>
            ) : null}
            <ThemedText style={styles.label}>Kg fiyatı (₺)</ThemedText>
            <TextInput
              value={pricePerKg}
              onChangeText={setPricePerKg}
              keyboardType="decimal-pad"
              placeholder="örn. 185.50"
              placeholderTextColor={palette.icon}
              style={[styles.input, { color: palette.text, borderColor: palette.icon + '55' }]}
            />
            <ThemedText style={styles.label}>Hayvan adedi (isteğe bağlı)</ThemedText>
            <TextInput
              value={animalCount}
              onChangeText={setAnimalCount}
              keyboardType="number-pad"
              placeholder="örn. 3"
              placeholderTextColor={palette.icon}
              style={[styles.input, { color: palette.text, borderColor: palette.icon + '55' }]}
            />
            <ThemedText style={styles.label}>Not</ThemedText>
            <TextInput
              value={note}
              onChangeText={setNote}
              multiline
              placeholder="İsteğe bağlı açıklama"
              placeholderTextColor={palette.icon}
              style={[
                styles.input,
                styles.textArea,
                { color: palette.text, borderColor: palette.icon + '55' },
              ]}
            />
            <View style={styles.modalActions}>
              <Pressable
                onPress={() => {
                  setOfferModalVisible(false);
                  setSelectedRequest(null);
                }}
                style={styles.secondaryBtn}>
                <ThemedText>Vazgeç</ThemedText>
              </Pressable>
              <Pressable
                onPress={submitOffer}
                disabled={submitting}
                style={[styles.primaryBtn, { backgroundColor: palette.tint, flex: 1 }]}>
                {submitting ? (
                  <ActivityIndicator color="#fff" />
                ) : (
                  <ThemedText style={styles.primaryBtnText}>Gönder</ThemedText>
                )}
              </Pressable>
            </View>
          </ThemedView>
        </View>
      </Modal>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 24, gap: 12 },
  scrollContent: { padding: 20, paddingBottom: 40 },
  subtitle: { marginTop: 8, opacity: 0.85 },
  muted: { opacity: 0.7, marginTop: 4 },
  sectionTitle: { marginTop: 20, marginBottom: 8 },
  sectionSpacer: { marginTop: 28 },
  card: {
    borderWidth: 1,
    borderRadius: 12,
    padding: 14,
    marginBottom: 12,
    gap: 4,
  },
  small: { fontSize: 14, opacity: 0.9 },
  smallMuted: { fontSize: 12, opacity: 0.6, marginTop: 4 },
  desc: { fontSize: 14, marginTop: 6, opacity: 0.85 },
  primaryBtn: {
    marginTop: 12,
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  primaryBtnText: { color: '#fff', fontWeight: '600' },
  errorBox: {
    marginTop: 12,
    padding: 12,
    borderRadius: 10,
    backgroundColor: 'rgba(220, 38, 38, 0.12)',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
  },
  errorText: { flex: 1, color: '#b91c1c', fontSize: 14 },
  dismissErr: { padding: 6 },
  dismissErrText: { color: '#0a7ea4', fontWeight: '600' },
  modalBackdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.45)',
    justifyContent: 'center',
    padding: 20,
  },
  modalCard: { borderRadius: 16, padding: 18, gap: 8 },
  modalHint: { opacity: 0.75, marginBottom: 8 },
  label: { fontSize: 13, fontWeight: '600', marginTop: 8 },
  input: {
    borderWidth: 1,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
  },
  textArea: { minHeight: 72, textAlignVertical: 'top' },
  modalActions: { flexDirection: 'row', gap: 12, marginTop: 16, alignItems: 'center' },
  secondaryBtn: { paddingVertical: 12, paddingHorizontal: 8 },
});
