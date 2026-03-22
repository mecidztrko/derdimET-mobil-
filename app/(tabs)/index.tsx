import { AdminHomeScreen } from '@/screens/AdminHomeScreen';
import { BuyerHomeScreen } from '@/screens/BuyerHomeScreen';
import { SellerHomeScreen } from '@/screens/SellerHomeScreen';
import { useAuth } from '@/contexts/AuthContext';
import type { UserRole } from '@/navigation/types';

function HomeByRole() {
  const { userRole } = useAuth();

  switch (userRole) {
    case 'ADMIN':
      return <AdminHomeScreen />;
    case 'ANIMAL_SELLER':
      return <SellerHomeScreen />;
    case 'MEAT_BUYER':
      return <BuyerHomeScreen />;
    default:
      return <AdminHomeScreen />;
  }
}

export default function HomeTab() {
  return <HomeByRole />;
}
