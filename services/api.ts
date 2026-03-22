/**
 * Backend API (web + mobil ortak).
 * - EXPO_PUBLIC_API_URL tanımlıysa o kullanılır.
 * - __DEV__ iken: Android emülatör → bilgisayardaki localhost için http://10.0.2.2:8080
 * - __DEV__ iken: iOS simülatör → http://localhost:8080
 * - Üretim: https://api.derdimet.com
 * Fiziksel Android telefon: EXPO_PUBLIC_API_URL=http://BILGISAYAR_LAN_IP:8080
 */

import { Platform } from 'react-native';

function resolveApiBaseUrl(): string {
  const fromEnv = process.env.EXPO_PUBLIC_API_URL?.trim().replace(/\/$/, '');
  if (fromEnv) return fromEnv;
  if (typeof __DEV__ !== 'undefined' && __DEV__) {
    return Platform.OS === 'android' ? 'http://10.0.2.2:8080' : 'http://localhost:8080';
  }
  return 'https://api.derdimet.com'.replace(/\/$/, '');
}

const API_BASE_URL = resolveApiBaseUrl();

export function getApiBaseUrl(): string {
  return API_BASE_URL;
}

let authToken: string | null = null;

export function setApiAuthToken(token: string | null) {
  authToken = token;
}

export function getApiAuthToken() {
  return authToken;
}

function authHeaders(): Record<string, string> {
  const h: Record<string, string> = { 'Content-Type': 'application/json' };
  if (authToken) {
    h.Authorization = `Bearer ${authToken}`;
  }
  return h;
}

export type ApiResponse<T> = {
  data: T;
  success: boolean;
  message?: string;
};

export type LoginResponse = {
  token: string;
  tokenType: string;
};

/** Spring {@code POST /api/auth/login} — başarılı olunca Bearer token bellekte tutulur. */
export async function loginWithEmailPassword(email: string, password: string): Promise<ApiResponse<LoginResponse>> {
  authToken = null;
  return apiPost<LoginResponse>('/api/auth/login', { email, password });
}

async function fetchOrThrow(input: string, init: RequestInit): Promise<Response> {
  try {
    return await fetch(input, init);
  } catch (e) {
    if (e instanceof TypeError && String(e.message).includes('Network request failed')) {
      throw new Error(
        `Sunucuya ulaşılamıyor (${API_BASE_URL}). Spring Boot’u 8080’de çalıştırın. Fiziksel cihazda: EXPO_PUBLIC_API_URL=http://<bilgisayar-IP>:8080`,
      );
    }
    throw e instanceof Error ? e : new Error('Ağ hatası');
  }
}

export async function apiGet<T>(endpoint: string): Promise<ApiResponse<T>> {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetchOrThrow(url, {
    method: 'GET',
    headers: authHeaders(),
  });
  const data = await parseJsonSafe(response);
  if (!response.ok) {
    throw new Error(readErrorMessage(data));
  }
  return { data: data as T, success: true };
}

export async function apiPost<T>(endpoint: string, body: unknown): Promise<ApiResponse<T>> {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetchOrThrow(url, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  const data = await parseJsonSafe(response);
  if (!response.ok) {
    throw new Error(readErrorMessage(data));
  }
  const payload = data as T;
  if (endpoint === '/api/auth/login' && data && typeof data === 'object' && data !== null && 'token' in data) {
    const t = (data as Record<string, unknown>).token;
    if (typeof t === 'string' && t.length > 0) {
      authToken = t;
    }
  }
  return { data: payload, success: true };
}

async function parseJsonSafe(response: Response): Promise<unknown> {
  const text = await response.text();
  if (!text) {
    return {};
  }
  try {
    return JSON.parse(text) as unknown;
  } catch {
    return { message: text };
  }
}

function readErrorMessage(data: unknown): string {
  if (data && typeof data === 'object' && 'message' in data) {
    const m = (data as { message?: unknown }).message;
    if (typeof m === 'string' && m.length > 0) {
      return m;
    }
  }
  return 'API hatası';
}
