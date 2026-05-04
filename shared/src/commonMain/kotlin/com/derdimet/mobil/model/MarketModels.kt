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
