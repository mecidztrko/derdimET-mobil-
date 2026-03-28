/** Backend {@code AnimalCategory} ile aynı anahtarlar */
export type AnimalCategory = 'KUCUKBAS' | 'BUYUKBAS';

/** Liste / ilan filtreleme: tümü veya tek kategori */
export type AnimalCategoryFilter = 'ALL' | AnimalCategory;

export const ANIMAL_CATEGORY_LABELS: Record<AnimalCategory, string> = {
  KUCUKBAS: 'Küçükbaş',
  BUYUKBAS: 'Büyükbaş',
};

export function labelForAnimalCategory(c: AnimalCategory | null | undefined): string {
  if (!c) return '—';
  return ANIMAL_CATEGORY_LABELS[c] ?? c;
}

/** Profil filtresine göre ilan / teklif satırı gösterilsin mi */
export function matchesAnimalCategoryFilter(
  category: AnimalCategory | null | undefined,
  filter: AnimalCategoryFilter,
): boolean {
  if (filter === 'ALL') return true;
  if (category == null) return false;
  return category === filter;
}
