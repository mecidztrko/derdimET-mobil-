/**
 * Backend {@code UserRole} ile aynı değerler
 */
export type UserRole = 'ADMIN' | 'ANIMAL_SELLER' | 'MEAT_BUYER';

export const USER_ROLE_LABELS: Record<UserRole, string> = {
  ADMIN: 'Yönetici',
  ANIMAL_SELLER: 'Hayvan satıcısı',
  MEAT_BUYER: 'Et alıcısı',
};

export const ROUTES = {
  SPLASH: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  TABS: '/(tabs)',
  HOME: '/(tabs)',
  EXPLORE: '/(tabs)/explore',
  PROFILE: '/(tabs)/profile',
  MODAL: '/modal',
} as const;
