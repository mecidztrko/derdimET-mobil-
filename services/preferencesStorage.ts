import * as SecureStore from 'expo-secure-store';

import type { AnimalCategoryFilter } from '@/constants/animalCategory';

/** Auth token ile aynı modül ama farklı anahtarlar; Android’de bozuk AsyncStorage Maven bağımlılığından kaçınılır. */

const KEYS = {
  REMEMBER: 'derdimet_pref_remember',
  EMAIL: 'derdimet_pref_email',
  PROFILE_SNAPSHOT: 'derdimet_pref_profile',
  ANIMAL_FILTER: 'derdimet_pref_animal_filter',
} as const;

export async function getRememberedEmail(): Promise<{ remember: boolean; email: string }> {
  try {
    const [r, e] = await Promise.all([
      SecureStore.getItemAsync(KEYS.REMEMBER),
      SecureStore.getItemAsync(KEYS.EMAIL),
    ]);
    return { remember: r === '1', email: e ?? '' };
  } catch {
    return { remember: false, email: '' };
  }
}

export async function setRememberPreference(remember: boolean, email: string): Promise<void> {
  try {
    if (remember) {
      await SecureStore.setItemAsync(KEYS.REMEMBER, '1');
      await SecureStore.setItemAsync(KEYS.EMAIL, email.trim());
    } else {
      await SecureStore.deleteItemAsync(KEYS.REMEMBER);
      await SecureStore.deleteItemAsync(KEYS.EMAIL);
    }
  } catch {
    // yok say
  }
}

export async function persistAuthProfileSnapshot(role: string, accountType: string): Promise<void> {
  try {
    await SecureStore.setItemAsync(
      KEYS.PROFILE_SNAPSHOT,
      JSON.stringify({ role, accountType, savedAt: Date.now() }),
    );
  } catch {
    // yok say
  }
}

export async function clearAuthProfileSnapshot(): Promise<void> {
  try {
    await SecureStore.deleteItemAsync(KEYS.PROFILE_SNAPSHOT);
  } catch {
    // yok say
  }
}

export async function getAnimalCategoryFilter(): Promise<AnimalCategoryFilter> {
  try {
    const v = await SecureStore.getItemAsync(KEYS.ANIMAL_FILTER);
    if (v === 'KUCUKBAS' || v === 'BUYUKBAS') return v;
    return 'ALL';
  } catch {
    return 'ALL';
  }
}

export async function setAnimalCategoryFilter(filter: AnimalCategoryFilter): Promise<void> {
  try {
    await SecureStore.setItemAsync(KEYS.ANIMAL_FILTER, filter);
  } catch {
    // yok say
  }
}
