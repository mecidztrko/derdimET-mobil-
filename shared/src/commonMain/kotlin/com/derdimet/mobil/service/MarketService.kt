package com.derdimet.mobil.service

import com.derdimet.mobil.model.*

class MarketService(private val apiService: ApiService) {

    suspend fun createAnimalPurchaseRequest(payload: CreateAnimalPurchasePayload): ApiResponse<AnimalPurchaseRequestDto> {
        // Yeni mantık: hayvan alım talebini kesimhane açar
        return apiService.post("/api/slaughterhouse/animal-purchase-requests", payload)
    }

    suspend fun fetchOpenAnimalPurchaseRequests(): ApiResponse<List<AnimalPurchaseRequestDto>> {
        return apiService.get("/api/seller/animal-purchase-requests")
    }

    suspend fun fetchOpenAnimalPurchaseRequestsFiltered(
        category: String? = null,
        q: String? = null,
        sort: String = "newest",
    ): ApiResponse<List<AnimalPurchaseRequestDto>> {
        val endpoint = buildString {
            append("/api/seller/animal-purchase-requests?sort=").append(sort)
            if (!q.isNullOrBlank()) append("&q=").append(q.trim())
            if (!category.isNullOrBlank()) append("&category=").append(category)
        }
        return apiService.get(endpoint)
    }

    suspend fun createAnimalOffer(requestId: Int, payload: CreateAnimalOfferPayload): ApiResponse<AnimalOfferResponseDto> {
        return apiService.post("/api/seller/animal-purchase-requests/$requestId/offers", payload)
    }

    suspend fun fetchMyAnimalOffers(): ApiResponse<List<SellerAnimalOfferItemDto>> {
        return apiService.get("/api/seller/animal-offers")
    }

    suspend fun fetchOpenMeatSaleRequests(): ApiResponse<List<MeatSaleRequestDto>> {
        return apiService.get("/api/buyer/meat-sale-requests")
    }

    suspend fun createBuyerMeatOffer(saleRequestId: Long, payload: CreateMeatOfferPayload): ApiResponse<BuyerMeatOfferItemDto> {
        return apiService.post("/api/buyer/meat-sale-requests/$saleRequestId/offers", payload)
    }

    suspend fun fetchMyBuyerMeatOffers(): ApiResponse<List<BuyerMeatOfferItemDto>> {
        return apiService.get("/api/buyer/meat-offers")
    }

    suspend fun fetchFavoriteSellers(): ApiResponse<List<FavoriteSellerDto>> {
        return apiService.get("/api/buyer/favorites")
    }

    suspend fun addFavoriteSeller(sellerId: Long): ApiResponse<Unit> {
        return apiService.post("/api/buyer/favorites/$sellerId", body = mapOf<String, String>())
    }

    suspend fun removeFavoriteSeller(sellerId: Long): ApiResponse<Unit> {
        return apiService.delete("/api/buyer/favorites/$sellerId")
    }

    suspend fun fetchMyPurchases(limit: Int = 10): ApiResponse<List<BuyerPurchaseItemDto>> {
        return apiService.get("/api/buyer/purchases?limit=$limit")
    }

    suspend fun fetchConversations(): ApiResponse<List<ConversationItemDto>> {
        return apiService.get("/api/conversations")
    }

    suspend fun getOrCreateConversation(otherUserId: Long): ApiResponse<ConversationItemDto> {
        return apiService.post("/api/conversations/with/$otherUserId", body = mapOf<String, String>())
    }

    suspend fun fetchMessages(conversationId: Long): ApiResponse<List<MessageDto>> {
        return apiService.get("/api/conversations/$conversationId/messages")
    }

    suspend fun sendMessage(conversationId: Long, text: String): ApiResponse<MessageDto> {
        return apiService.post("/api/conversations/$conversationId/messages", body = CreateMessagePayload(text = text))
    }
}
