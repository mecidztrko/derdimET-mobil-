package com.derdimet.mobil.model

import kotlinx.serialization.Serializable

@Serializable
enum class RequestStatus { OPEN, CLOSED }

@Serializable
enum class OfferStatus { PENDING, ACCEPTED, REJECTED }

@Serializable
enum class AnimalCategory { KUCUKBAS, BUYUKBAS }

@Serializable
data class AnimalPurchaseRequestDto(
    val id: Int,
    val title: String,
    val animalCategory: AnimalCategory? = null,
    val quantity: Int? = null,
    val expectedWeight: Double? = null,
    val description: String? = null,
    val status: RequestStatus,
    val createdAt: String
)

@Serializable
data class SellerAnimalOfferItemDto(
    val offerId: Int,
    val request: AnimalPurchaseRequestDto,
    val pricePerKg: Double,
    val animalCount: Int? = null,
    val note: String? = null,
    val status: OfferStatus,
    val createdAt: String
)

@Serializable
data class CreateAnimalOfferPayload(
    val pricePerKg: Double,
    val animalCount: Int? = null,
    val note: String? = null
)

@Serializable
data class CreateAnimalPurchasePayload(
    val title: String,
    val animalCategory: AnimalCategory,
    val quantity: Int? = null,
    val expectedWeight: Double? = null,
    val description: String? = null
)

@Serializable
data class AnimalOfferResponseDto(
    val id: Int,
    val requestId: Int,
    val pricePerKg: Double,
    val animalCount: Int? = null,
    val note: String? = null,
    val status: OfferStatus,
    val createdAt: String
)

@Serializable
data class MeatSaleRequestDto(
    val id: Long,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val title: String,
    val meatType: String,
    val quantity: Double? = null,
    val description: String? = null,
    val status: RequestStatus,
    val createdAt: String,
)

@Serializable
data class CreateMeatOfferPayload(
    val pricePerKg: Double,
    val quantity: Double,
    val note: String? = null,
)

@Serializable
data class BuyerMeatOfferItemDto(
    val offerId: Long,
    val saleRequestId: Long,
    val title: String? = null,
    val meatType: String? = null,
    val requestedQuantity: Double? = null,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val pricePerKg: Double? = null,
    val quantity: Double? = null,
    val note: String? = null,
    val status: OfferStatus,
    val createdAt: String,
)

@Serializable
data class FavoriteSellerDto(
    val sellerId: Long,
    val sellerName: String? = null,
    val sellerEmail: String? = null,
    val createdAt: String,
)

@Serializable
data class BuyerPurchaseItemDto(
    val orderId: Long,
    val totalPrice: Double? = null,
    val status: String,
    val createdAt: String,
)

@Serializable
data class ConversationItemDto(
    val conversationId: Long,
    val otherUserId: Long,
    val otherUserName: String? = null,
    val otherUserEmail: String? = null,
    val lastMessageAt: String? = null,
)

@Serializable
data class MessageDto(
    val id: Long,
    val senderId: Long,
    val senderName: String? = null,
    val text: String,
    val createdAt: String,
    val readAt: String? = null,
)

@Serializable
data class CreateMessagePayload(
    val text: String,
)
