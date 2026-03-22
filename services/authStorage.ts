import * as SecureStore from 'expo-secure-store';

const KEY = 'derdimet_auth_token';

export async function getStoredAuthToken(): Promise<string | null> {
  try {
    return await SecureStore.getItemAsync(KEY);
  } catch {
    return null;
  }
}

export async function setStoredAuthToken(token: string): Promise<void> {
  await SecureStore.setItemAsync(KEY, token);
}

export async function clearStoredAuthToken(): Promise<void> {
  try {
    await SecureStore.deleteItemAsync(KEY);
  } catch {
    // yok say
  }
}
