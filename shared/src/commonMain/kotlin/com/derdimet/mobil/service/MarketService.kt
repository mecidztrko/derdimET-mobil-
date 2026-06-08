package com.derdimet.mobil.service

import com.derdimet.mobil.model.*

class MarketService(private val apiService: ApiService) {
    suspend fun fetchMe(): ApiResponse<MeResponse> {
        return apiService.me()
    }

    suspend fun fetchSlaughterhouseFavoriteSellers(): ApiResponse<List<FavoriteSellerDto>> {
        return apiService.get("/api/slaughterhouse/profile/favorites/sellers")
    }

    suspend fun addSlaughterhouseFavoriteSeller(sellerId: Long): ApiResponse<Unit> {
        return apiService.post("/api/slaughterhouse/profile/favorites/sellers/$sellerId", body = mapOf<String, String>())
    }

    suspend fun removeSlaughterhouseFavoriteSeller(sellerId: Long): ApiResponse<Unit> {
        return apiService.delete("/api/slaughterhouse/profile/favorites/sellers/$sellerId")
    }

    suspend fun fetchSlaughterhouseFavoriteBuyers(): ApiResponse<List<FavoriteMeatBuyerDto>> {
        return apiService.get("/api/slaughterhouse/profile/favorites/buyers")
    }

    suspend fun addSlaughterhouseFavoriteBuyer(buyerId: Long): ApiResponse<Unit> {
        return apiService.post("/api/slaughterhouse/profile/favorites/buyers/$buyerId", body = mapOf<String, String>())
    }

    suspend fun removeSlaughterhouseFavoriteBuyer(buyerId: Long): ApiResponse<Unit> {
        return apiService.delete("/api/slaughterhouse/profile/favorites/buyers/$buyerId")
    }

    suspend fun fetchSlaughterhousePurchases(limit: Int = 10): ApiResponse<List<SlaughterhousePurchaseItemDto>> {
        return apiService.get("/api/slaughterhouse/profile/purchases?limit=$limit")
    }

    suspend fun fetchSlaughterhouseSales(limit: Int = 10): ApiResponse<List<SlaughterhouseSaleItemDto>> {
        return apiService.get("/api/slaughterhouse/profile/sales?limit=$limit")
    }

    suspend fun searchSlaughterhouseAnimalListingsFiltered(
        category: String? = null,
        type: String? = null,
        ageMin: Int? = null,
        ageMax: Int? = null,
        quantityMin: Int? = null,
        quantityMax: Int? = null,
        priceMin: Double? = null,
        priceMax: Double? = null,
        sort: String = "newest",
    ): ApiResponse<List<SellerAnimalListingDto>> {
        val endpoint = buildString {
            append("/api/slaughterhouse/animal-listings?sort=").append(sort)
            if (!category.isNullOrBlank()) append("&category=").append(category)
            if (!type.isNullOrBlank()) append("&type=").append(type.trim())
            if (ageMin != null) append("&ageMin=").append(ageMin)
            if (ageMax != null) append("&ageMax=").append(ageMax)
            if (quantityMin != null) append("&quantityMin=").append(quantityMin)
            if (quantityMax != null) append("&quantityMax=").append(quantityMax)
            if (priceMin != null) append("&priceMin=").append(priceMin)
            if (priceMax != null) append("&priceMax=").append(priceMax)
        }
        return apiService.get(endpoint)
    }

    suspend fun createSlaughterhouseListingOffer(
        listingId: Long,
        payload: CreateSlaughterhouseListingOfferPayload,
    ): ApiResponse<SlaughterhouseListingOfferDto> {
        return apiService.post("/api/slaughterhouse/animal-listings/$listingId/offers", payload)
    }

    suspend fun fetchMySlaughterhouseListingOffers(): ApiResponse<List<SlaughterhouseListingOfferDto>> {
        return apiService.get("/api/slaughterhouse/offers")
    }

    suspend fun createSlaughterhouseMeatSaleRequest(
        payload: CreateMeatSaleRequestPayload,
    ): ApiResponse<MeatSaleRequestDto> {
        return apiService.post("/api/slaughterhouse/meat-sale-requests", payload)
    }

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

    suspend fun fetchSellerFavoriteBuyers(): ApiResponse<List<FavoriteBuyerDto>> {
        return apiService.get("/api/seller/profile/favorites")
    }

    suspend fun addSellerFavoriteBuyer(buyerId: Long): ApiResponse<Unit> {
        return apiService.post("/api/seller/profile/favorites/$buyerId", body = mapOf<String, String>())
    }

    suspend fun removeSellerFavoriteBuyer(buyerId: Long): ApiResponse<Unit> {
        return apiService.delete("/api/seller/profile/favorites/$buyerId")
    }

    suspend fun fetchSellerSales(limit: Int = 10): ApiResponse<List<SellerSaleItemDto>> {
        return apiService.get("/api/seller/profile/sales?limit=$limit")
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

    suspend fun createSellerAnimalListing(payload: CreateSellerAnimalListingPayload): ApiResponse<SellerAnimalListingDto> {
        return apiService.post("/api/seller/animal-listings", payload)
    }

    suspend fun fetchMySellerAnimalListings(): ApiResponse<List<SellerAnimalListingDto>> {
        return apiService.get("/api/seller/animal-listings")
    }

    suspend fun fetchAnimalListingDetail(id: Long): ApiResponse<SellerAnimalListingDto> {
        return apiService.get("/api/listings/animal/$id")
    }

    suspend fun fetchMeatSaleRequestDetail(id: Long): ApiResponse<MeatSaleRequestDto> {
        return apiService.get("/api/listings/meat/$id")
    }

    suspend fun fetchAnimalPurchaseRequestDetail(id: Long): ApiResponse<AnimalPurchaseRequestDto> {
        return apiService.get("/api/listings/animal-request/$id")
    }

    suspend fun fetchPublicProfile(userId: Long): ApiResponse<PublicUserProfileDto> {
        return apiService.get("/api/users/$userId/public")
    }

    suspend fun toggleFavorite(targetUserId: Long): ApiResponse<FavoriteToggleResponse> {
        return apiService.post("/api/favorites/toggle/$targetUserId", body = mapOf<String, String>())
    }

    suspend fun fetchBuyerFavoriteSlaughterhouses(): ApiResponse<List<FavoriteSlaughterhouseDto>> {
        return apiService.get("/api/buyer/favorite-slaughterhouses")
    }

    suspend fun uploadImage(
        bytes: ByteArray,
        filename: String,
        contentType: String,
    ): ApiResponse<UploadedImageResponse> {
        return apiService.uploadImage(bytes, filename, contentType)
    }

    suspend fun fetchSlaughterhouseIncomingMeatOffers(): ApiResponse<List<SlaughterhouseIncomingMeatOfferDto>> {
        return apiService.get("/api/slaughterhouse/meat-offers")
    }

    suspend fun acceptSlaughterhouseMeatOffer(offerId: Long): ApiResponse<SlaughterhouseIncomingMeatOfferDto> {
        return apiService.post("/api/slaughterhouse/meat-offers/$offerId/accept", body = mapOf<String, String>())
    }

    suspend fun rejectSlaughterhouseMeatOffer(offerId: Long): ApiResponse<SlaughterhouseIncomingMeatOfferDto> {
        return apiService.post("/api/slaughterhouse/meat-offers/$offerId/reject", body = mapOf<String, String>())
    }

    suspend fun fetchSellerIncomingListingOffers(): ApiResponse<List<SellerIncomingListingOfferDto>> {
        return apiService.get("/api/seller/incoming-listing-offers")
    }

    suspend fun acceptSellerListingOffer(offerId: Long): ApiResponse<SellerIncomingListingOfferDto> {
        return apiService.post("/api/seller/listing-offers/$offerId/accept", body = mapOf<String, String>())
    }

    suspend fun rejectSellerListingOffer(offerId: Long): ApiResponse<SellerIncomingListingOfferDto> {
        return apiService.post("/api/seller/listing-offers/$offerId/reject", body = mapOf<String, String>())
    }

    suspend fun fetchConversationOffers(conversationId: Long): ApiResponse<List<ConversationOfferDto>> {
        return apiService.get("/api/messaging/conversations/$conversationId/offers")
    }

    suspend fun fetchMySlaughterhouseMeatSaleRequests(): ApiResponse<List<MeatSaleRequestDto>> {
        return apiService.get("/api/slaughterhouse/meat-sale-requests")
    }

    suspend fun fetchMySlaughterhouseAnimalPurchaseRequests(): ApiResponse<List<AnimalPurchaseRequestDto>> {
        return apiService.get("/api/slaughterhouse/animal-purchase-requests")
    }

    suspend fun fetchNotificationSummary(): ApiResponse<NotificationSummaryDto> {
        return apiService.get("/api/notifications/summary")
    }

    suspend fun closeSellerAnimalListing(listingId: Long): ApiResponse<SellerAnimalListingDto> {
        return apiService.post("/api/seller/animal-listings/$listingId/close", body = mapOf<String, String>())
    }

    suspend fun reopenSellerAnimalListing(listingId: Long): ApiResponse<SellerAnimalListingDto> {
        return apiService.post("/api/seller/animal-listings/$listingId/reopen", body = mapOf<String, String>())
    }

    suspend fun updateSellerAnimalListing(
        listingId: Long,
        payload: UpdateSellerAnimalListingPayload,
    ): ApiResponse<SellerAnimalListingDto> {
        return apiService.patch("/api/seller/animal-listings/$listingId", payload)
    }

    suspend fun closeMeatSaleRequest(saleRequestId: Long): ApiResponse<MeatSaleRequestDto> {
        return apiService.post("/api/slaughterhouse/meat-sale-requests/$saleRequestId/close", body = mapOf<String, String>())
    }

    suspend fun reopenMeatSaleRequest(saleRequestId: Long): ApiResponse<MeatSaleRequestDto> {
        return apiService.post("/api/slaughterhouse/meat-sale-requests/$saleRequestId/reopen", body = mapOf<String, String>())
    }

    suspend fun updateMeatSaleRequest(
        saleRequestId: Long,
        payload: UpdateMeatSaleRequestPayload,
    ): ApiResponse<MeatSaleRequestDto> {
        return apiService.patch("/api/slaughterhouse/meat-sale-requests/$saleRequestId", payload)
    }

    suspend fun closeAnimalPurchaseRequest(requestId: Long): ApiResponse<AnimalPurchaseRequestDto> {
        return apiService.post("/api/slaughterhouse/animal-purchase-requests/$requestId/close", body = mapOf<String, String>())
    }

    suspend fun reopenAnimalPurchaseRequest(requestId: Long): ApiResponse<AnimalPurchaseRequestDto> {
        return apiService.post("/api/slaughterhouse/animal-purchase-requests/$requestId/reopen", body = mapOf<String, String>())
    }

    suspend fun updateAnimalPurchaseRequest(
        requestId: Long,
        payload: UpdateAnimalPurchasePayload,
    ): ApiResponse<AnimalPurchaseRequestDto> {
        return apiService.patch("/api/slaughterhouse/animal-purchase-requests/$requestId", payload)
    }
}
