package com.derdimet.mobil.service

import com.derdimet.mobil.model.*

class MarketService(private val apiService: ApiService) {

    suspend fun createAnimalPurchaseRequest(payload: CreateAnimalPurchasePayload): ApiResponse<AnimalPurchaseRequestDto> {
        return apiService.post("/api/admin/animal-purchase-requests", payload)
    }

    suspend fun fetchOpenAnimalPurchaseRequests(): ApiResponse<List<AnimalPurchaseRequestDto>> {
        return apiService.get("/api/seller/animal-purchase-requests")
    }

    suspend fun createAnimalOffer(requestId: Int, payload: CreateAnimalOfferPayload): ApiResponse<AnimalOfferResponseDto> {
        return apiService.post("/api/seller/animal-purchase-requests/$requestId/offers", payload)
    }

    suspend fun fetchMyAnimalOffers(): ApiResponse<List<SellerAnimalOfferItemDto>> {
        return apiService.get("/api/seller/animal-offers")
    }
}
