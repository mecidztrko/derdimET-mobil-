import { apiGet, apiPost } from '@/services/api';
import type {
  AnimalOfferResponseDto,
  AnimalPurchaseRequestDto,
  CreateAnimalOfferPayload,
  SellerAnimalOfferItemDto,
} from '@/types/sellerMarket';

/** Açık hayvan alış ilanları (yönetici ekleri). */
export async function fetchOpenAnimalPurchaseRequests(): Promise<AnimalPurchaseRequestDto[]> {
  const { data } = await apiGet<AnimalPurchaseRequestDto[]>('/api/seller/animal-purchase-requests');
  return data;
}

export async function createAnimalOffer(
  requestId: number,
  payload: CreateAnimalOfferPayload,
): Promise<AnimalOfferResponseDto> {
  const { data } = await apiPost<AnimalOfferResponseDto>(
    `/api/seller/animal-purchase-requests/${requestId}/offers`,
    payload,
  );
  return data;
}

export async function fetchMyAnimalOffers(): Promise<SellerAnimalOfferItemDto[]> {
  const { data } = await apiGet<SellerAnimalOfferItemDto[]>('/api/seller/animal-offers');
  return data;
}
