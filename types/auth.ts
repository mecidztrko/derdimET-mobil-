import type { UserRole } from '@/navigation/types';

export type AuthUser = {
  id: number;
  email: string;
  name: string;
  role: UserRole;
  phone: string | null;
  accountType: string;
  companyName: string | null;
  taxNumber: string | null;
  addressLine: string | null;
  city: string | null;
  profileImageUrl: string | null;
  businessVerified: boolean;
};
