import type { UserRole } from './types';
import { ROUTES } from './types';

/**
 * Rol bazlı başlangıç yönlendirmesi.
 * Her rol için ana ekran (tabs) aynı; içerik role göre değişir.
 */
export function getInitialRouteForRole(_role: UserRole): string {
  return ROUTES.TABS;
}
