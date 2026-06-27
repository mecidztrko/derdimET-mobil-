package com.derdimet.mobil.repository

import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.SellerAnimalListingDto
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Son görülen ilan listelerini yerel depoda tutar (offline önizleme). */
class ListingCacheRepository(private val storage: SettingsStorage) {
    private val json = Json { ignoreUnknownKeys = true }

    fun saveMeatListings(listings: List<MeatSaleRequestDto>) {
        if (listings.isEmpty()) return
        storage.setString(KEY_MEAT, json.encodeToString(ListSerializer(MeatSaleRequestDto.serializer()), listings))
    }

    fun loadMeatListings(): List<MeatSaleRequestDto> {
        val raw = storage.getString(KEY_MEAT) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(MeatSaleRequestDto.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    fun saveAnimalListings(listings: List<SellerAnimalListingDto>) {
        if (listings.isEmpty()) return
        storage.setString(KEY_ANIMAL, json.encodeToString(ListSerializer(SellerAnimalListingDto.serializer()), listings))
    }

    fun loadAnimalListings(): List<SellerAnimalListingDto> {
        val raw = storage.getString(KEY_ANIMAL) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(SellerAnimalListingDto.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    fun saveConversations(conversations: List<ConversationItemDto>) {
        if (conversations.isEmpty()) return
        storage.setString(KEY_CONVERSATIONS, json.encodeToString(ListSerializer(ConversationItemDto.serializer()), conversations))
    }

    fun loadConversations(): List<ConversationItemDto> {
        val raw = storage.getString(KEY_CONVERSATIONS) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(ConversationItemDto.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    fun savePurchaseRequests(requests: List<AnimalPurchaseRequestDto>) {
        if (requests.isEmpty()) return
        storage.setString(KEY_PURCHASE_REQUESTS, json.encodeToString(ListSerializer(AnimalPurchaseRequestDto.serializer()), requests))
    }

    fun loadPurchaseRequests(): List<AnimalPurchaseRequestDto> {
        val raw = storage.getString(KEY_PURCHASE_REQUESTS) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(AnimalPurchaseRequestDto.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    fun saveMySellerListings(listings: List<SellerAnimalListingDto>) {
        if (listings.isEmpty()) return
        storage.setString(KEY_MY_SELLER_LISTINGS, json.encodeToString(ListSerializer(SellerAnimalListingDto.serializer()), listings))
    }

    fun loadMySellerListings(): List<SellerAnimalListingDto> {
        val raw = storage.getString(KEY_MY_SELLER_LISTINGS) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(SellerAnimalListingDto.serializer()), raw)
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val KEY_MEAT = "derdimet_cache_meat_listings"
        const val KEY_ANIMAL = "derdimet_cache_animal_listings"
        const val KEY_CONVERSATIONS = "derdimet_cache_conversations"
        const val KEY_PURCHASE_REQUESTS = "derdimet_cache_purchase_requests"
        const val KEY_MY_SELLER_LISTINGS = "derdimet_cache_my_seller_listings"
    }
}
