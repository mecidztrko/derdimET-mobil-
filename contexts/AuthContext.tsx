'use client';

import React, { createContext, useContext, useState, useCallback } from 'react';
import type { UserRole } from '@/navigation/types';

type AuthContextType = {
  userRole: UserRole | null;
  setUserRole: (role: UserRole | null) => void;
  isAuthenticated: boolean;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [userRole, setUserRoleState] = useState<UserRole | null>(null);

  const setUserRole = useCallback((role: UserRole | null) => {
    setUserRoleState(role);
  }, []);

  const logout = useCallback(() => {
    setUserRoleState(null);
  }, []);

  const value: AuthContextType = {
    userRole,
    setUserRole,
    isAuthenticated: userRole !== null,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
