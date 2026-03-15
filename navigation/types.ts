/**
 * Kullanıcı rolleri - DerdimET uygulaması
 */
export type UserRole = 'admin' | 'hayvan_satici' | 'et_alici';

export const USER_ROLE_LABELS: Record<UserRole, string> = {
  admin: 'Admin',
  hayvan_satici: 'Hayvan Satıcı',
  et_alici: 'Et Alıcı',
};

/**
 * Stack ve Tab route isimleri
 */
export const ROUTES = {
  ROLE_SELECT: '/',
  TABS: '/(tabs)',
  HOME: '/(tabs)',
  EXPLORE: '/(tabs)/explore',
  PROFILE: '/(tabs)/profile',
  MODAL: '/modal',
} as const;
