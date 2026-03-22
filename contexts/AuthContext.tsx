'use client';

import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import type { UserRole } from '@/navigation/types';
import { apiGet, apiPost, getApiAuthToken, loginWithEmailPassword, setApiAuthToken } from '@/services/api';
import { clearStoredAuthToken, getStoredAuthToken, setStoredAuthToken } from '@/services/authStorage';
import {
  clearAuthProfileSnapshot,
  persistAuthProfileSnapshot,
} from '@/services/preferencesStorage';
import type { AuthUser } from '@/types/auth';
import { meResponseToUser, type MeResponse } from '@/types/me';

export type RegisterPayload = {
  email: string;
  password: string;
  name: string;
  phone?: string;
  role: 'ANIMAL_SELLER' | 'MEAT_BUYER';
  accountType: 'INDIVIDUAL' | 'BUSINESS';
  companyName?: string;
  taxNumber?: string;
  addressLine?: string;
  city?: string;
};

type AuthContextType = {
  user: AuthUser | null;
  userRole: UserRole | null;
  isAuthenticated: boolean;
  bootstrapping: boolean;
  login: (email: string, password: string) => Promise<AuthUser>;
  registerAccount: (payload: RegisterPayload) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [bootstrapping, setBootstrapping] = useState(true);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const token = await getStoredAuthToken();
        if (!token) {
          if (!cancelled) setBootstrapping(false);
          return;
        }
        setApiAuthToken(token);
        const { data } = await apiGet<MeResponse>('/api/me');
        if (cancelled) return;
        const parsed = meResponseToUser(data);
        if ('user' in parsed) {
          setUser(parsed.user);
          await persistAuthProfileSnapshot(parsed.user.role, parsed.user.accountType);
        } else {
          await clearStoredAuthToken();
          setApiAuthToken(null);
        }
      } catch {
        await clearStoredAuthToken();
        setApiAuthToken(null);
      } finally {
        if (!cancelled) setBootstrapping(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    await loginWithEmailPassword(email.trim(), password);
    const token = getApiAuthToken();
    if (token) {
      await setStoredAuthToken(token);
    }
    const { data } = await apiGet<MeResponse>('/api/me');
    const parsed = meResponseToUser(data);
    if ('error' in parsed) {
      throw new Error(parsed.error);
    }
    setUser(parsed.user);
    await persistAuthProfileSnapshot(parsed.user.role, parsed.user.accountType);
    return parsed.user;
  }, []);

  const registerAccount = useCallback(async (payload: RegisterPayload) => {
    const body: Record<string, unknown> = {
      email: payload.email.trim(),
      password: payload.password,
      name: payload.name.trim(),
      role: payload.role,
      accountType: payload.accountType,
    };
    const phone = payload.phone?.trim();
    if (phone) body.phone = phone;
    if (payload.accountType === 'BUSINESS') {
      body.companyName = payload.companyName?.trim();
      body.taxNumber = payload.taxNumber?.trim();
    }
    const addr = payload.addressLine?.trim();
    const city = payload.city?.trim();
    if (addr) body.addressLine = addr;
    if (city) body.city = city;

    await apiPost('/api/register', body);
    await login(payload.email.trim(), payload.password);
  }, [login]);

  const logout = useCallback(async () => {
    await clearStoredAuthToken();
    await clearAuthProfileSnapshot();
    setApiAuthToken(null);
    setUser(null);
  }, []);

  const refreshUser = useCallback(async () => {
    const token = getApiAuthToken();
    if (!token) return;
    const { data } = await apiGet<MeResponse>('/api/me');
    const parsed = meResponseToUser(data);
    if ('user' in parsed) {
      setUser(parsed.user);
      await persistAuthProfileSnapshot(parsed.user.role, parsed.user.accountType);
    }
  }, []);

  const value = useMemo<AuthContextType>(
    () => ({
      user,
      userRole: user?.role ?? null,
      isAuthenticated: user !== null,
      bootstrapping,
      login,
      registerAccount,
      logout,
      refreshUser,
    }),
    [user, bootstrapping, login, registerAccount, logout, refreshUser],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
