/**
 * Backend API çağrıları için temel servis.
 * İleride base URL ve auth token buradan yönetilecek.
 */

const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL ?? 'https://api.derdimet.com';

export type ApiResponse<T> = {
  data: T;
  success: boolean;
  message?: string;
};

export async function apiGet<T>(endpoint: string): Promise<ApiResponse<T>> {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      // 'Authorization': `Bearer ${token}`,
    },
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.message ?? 'API hatası');
  }
  return { data, success: true };
}

export async function apiPost<T>(endpoint: string, body: unknown): Promise<ApiResponse<T>> {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      // 'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(body),
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data?.message ?? 'API hatası');
  }
  return { data, success: true };
}
