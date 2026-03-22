import type { AuthUser } from '@/types/auth';
import type { UserRole } from '@/navigation/types';

/** Spring {@code MeResponse} JSON */
export type MeResponse = {
  id: number;
  email: string;
  name: string;
  role: string;
  phone: string | null;
  accountType: string;
  companyName: string | null;
  taxNumber: string | null;
  addressLine: string | null;
  city: string | null;
  contactSecondaryName: string | null;
  contactSecondaryPhone: string | null;
  profileImageUrl: string | null;
  businessVerified: boolean;
};

export function parseUserRoleFromApi(role: string): UserRole | null {
  if (role === 'ADMIN' || role === 'ANIMAL_SELLER' || role === 'MEAT_BUYER') {
    return role;
  }
  return null;
}

export function meResponseToUser(me: MeResponse): { user: AuthUser } | { error: string } {
  const role = parseUserRoleFromApi(me.role);
  if (!role) {
    return { error: 'Sunucudan geçersiz rol bilgisi alındı' };
  }
  return {
    user: {
      id: me.id,
      email: me.email,
      name: me.name,
      role,
      phone: me.phone,
      accountType: me.accountType,
      companyName: me.companyName,
      taxNumber: me.taxNumber,
      addressLine: me.addressLine,
      city: me.city,
      profileImageUrl: me.profileImageUrl,
      businessVerified: me.businessVerified,
    },
  };
}
