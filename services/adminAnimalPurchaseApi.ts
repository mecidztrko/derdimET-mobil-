import { apiPost } from '@/services/api';
import type { AnimalPurchaseRequestDto, CreateAnimalPurchasePayload } from '@/types/sellerMarket';

/** Yönetici: hayvan alış ilanı oluşturur. */
export async function createAnimalPurchaseRequest(
  payload: CreateAnimalPurchasePayload,
): Promise<AnimalPurchaseRequestDto> {
  const { data } = await apiPost<AnimalPurchaseRequestDto>('/api/admin/animal-purchase-requests', payload);
  return data;
}
