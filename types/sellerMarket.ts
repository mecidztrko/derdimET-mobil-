/** Spring enum + DTO ile uyumlu */

import type { AnimalCategory } from '@/constants/animalCategory';

export type RequestStatus = 'OPEN' | 'CLOSED';

export type OfferStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

export type AnimalPurchaseRequestDto = {
  id: number;
  title: string;
  animalCategory: AnimalCategory | null;
  quantity: number | null;
  expectedWeight: number | null;
  description: string | null;
  status: RequestStatus;
  createdAt: string;
};

export type SellerAnimalOfferItemDto = {
  offerId: number;
  request: AnimalPurchaseRequestDto;
  pricePerKg: number;
  animalCount: number | null;
  note: string | null;
  status: OfferStatus;
  createdAt: string;
};

export type CreateAnimalOfferPayload = {
  pricePerKg: number;
  animalCount?: number | null;
  note?: string | null;
};

export type CreateAnimalPurchasePayload = {
  title: string;
  animalCategory: AnimalCategory;
  quantity?: number | null;
  expectedWeight?: number | null;
  description?: string | null;
};

export type AnimalOfferResponseDto = {
  id: number;
  requestId: number;
  pricePerKg: number;
  animalCount: number | null;
  note: string | null;
  status: OfferStatus;
  createdAt: string;
};
