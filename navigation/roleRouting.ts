import type { Href } from 'expo-router';
import type { UserRole } from './types';
import { ROUTES } from './types';

/**
 * Rol bazlı başlangıç yönlendirmesi.
 * Ana tab kökü aynı; sekme içi içerik role göre değişir (Buyer / Seller / Admin).
 */
export function getInitialRouteForRole(_role: UserRole): Href {
  return ROUTES.TABS as Href;
}
