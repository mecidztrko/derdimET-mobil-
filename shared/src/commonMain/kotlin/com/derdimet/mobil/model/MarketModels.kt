package com.derdimet.mobil.model

import kotlinx.serialization.Serializable

@Serializable
enum class RequestStatus { OPEN, CLOSED }

@Serializable
enum class OfferStatus { PENDING, ACCEPTED, REJECTED }

@Serializable
enum class AnimalCategory { KUCUKBAS, BUYUKBAS }

@Serializable
enum class ListingClosedReason { MANUAL, SOLD, EXPIRED, CANCELLED }

@Serializable
enum class NotificationType { OFFER, MESSAGE, LISTING, PAYMENT, SYSTEM }

@Serializable
enum class OfferEventType { CREATED, REVISED }

@Serializable
enum class OfferKind { MEAT, LISTING, ANIMAL }

@Serializable
data class AnimalPurchaseRequestDto(
    val id: Int,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val slaughterhouseCompanyName: String? = null,
    val slaughterhouseCity: String? = null,
    val title: String,
    val animalCategory: AnimalCategory? = null,
    val quantity: Int? = null,
    val expectedWeight: Double? = null,
    val description: String? = null,
    val status: RequestStatus,
    val closedReason: ListingClosedReason? = null,
    val expiresAt: String? = null,
    val createdAt: String,
    val isFavoritedByMe: Boolean? = null,
)

@Serializable
data class SellerAnimalOfferItemDto(
    val offerId: Int,
    val request: AnimalPurchaseRequestDto,
    val pricePerKg: Double,
    val animalCount: Int? = null,
    val note: String? = null,
    val status: OfferStatus,
    val revisionNumber: Int? = null,
    val expiresAt: String? = null,
    val createdAt: String
)

@Serializable
data class ReviseOfferPayload(
    val pricePerKg: Double,
    val quantity: Double? = null,
    val note: String? = null,
)

@Serializable
data class OfferEventDto(
    val id: Long,
    val offerKind: OfferKind,
    val offerId: Long,
    val eventType: OfferEventType,
    val pricePerKg: Double? = null,
    val quantity: Double? = null,
    val note: String? = null,
    val revisionNumber: Int? = null,
    val createdAt: String,
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
    val slaughterhouseCompanyName: String? = null,
    val slaughterhouseCity: String? = null,
    val title: String,
    val meatType: String,
    val animalCategory: AnimalCategory? = null,
    val cut: String? = null,
    val quantity: Double? = null,
    val pricePerKg: Double? = null,
    val packaging: String? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrls: List<String> = emptyList(),
    val status: RequestStatus,
    val closedReason: ListingClosedReason? = null,
    val expiresAt: String? = null,
    val createdAt: String,
    val isFavoritedByMe: Boolean? = null,
)

@Serializable
data class CreateMeatOfferPayload(
    val pricePerKg: Double,
    val quantity: Double,
    val note: String? = null,
)

@Serializable
data class CreateMeatSaleRequestPayload(
    val title: String,
    val meatType: String,
    val animalCategory: AnimalCategory? = null,
    val cut: String? = null,
    val quantity: Double,
    val pricePerKg: Double? = null,
    val packaging: String? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrls: List<String>? = null,
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
    val revisionNumber: Int? = null,
    val expiresAt: String? = null,
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
    val orderId: Long? = null,
    val meatOfferId: Long? = null,
    val saleRequestId: Long? = null,
    val saleTitle: String? = null,
    val meatType: String? = null,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val slaughterhouseCompanyName: String? = null,
    val pricePerKg: Double? = null,
    val quantity: Double? = null,
    val totalPrice: Double? = null,
    val status: String,
    val createdAt: String,
)

@Serializable
data class FavoriteBuyerDto(
    val buyerId: Long,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val createdAt: String,
)

@Serializable
data class SellerSaleItemDto(
    val offerId: Long,
    val saleType: String? = null,
    val requestId: Long? = null,
    val requestTitle: String? = null,
    val listingId: Long? = null,
    val listingTitle: String? = null,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val slaughterhouseCompanyName: String? = null,
    val pricePerKg: Double? = null,
    val animalCount: Int? = null,
    val estimatedTotal: Double? = null,
    val status: OfferStatus,
    val createdAt: String,
)

@Serializable
data class ConversationItemDto(
    val conversationId: Long,
    val otherUserId: Long,
    val otherUserName: String? = null,
    val otherUserEmail: String? = null,
    val otherUserRole: String? = null,
    val lastMessageAt: String? = null,
    val unreadCount: Int = 0,
)

@Serializable
data class ConversationOfferDto(
    val kind: String,
    val offerId: Long,
    val title: String? = null,
    val subtitle: String? = null,
    val pricePerKg: Double? = null,
    val animalCount: Int? = null,
    val quantityKg: Double? = null,
    val note: String? = null,
    val status: OfferStatus,
    val createdAt: String,
    val incoming: Boolean = false,
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

@Serializable
data class SellerAnimalListingDto(
    val id: Long,
    val sellerId: Long? = null,
    val sellerName: String? = null,
    val sellerCompanyName: String? = null,
    val sellerCity: String? = null,
    val category: AnimalCategory,
    val type: String,
    val breed: String? = null,
    val ageMonths: Int? = null,
    val quantity: Int,
    val avgWeightKg: Double? = null,
    val price: Double? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrls: List<String> = emptyList(),
    val status: RequestStatus,
    val closedReason: ListingClosedReason? = null,
    val expiresAt: String? = null,
    val createdAt: String,
    val isFavoritedByMe: Boolean? = null,
)

@Serializable
data class NotificationSummaryDto(
    val pendingOffers: Int = 0,
    val pendingIncoming: Int = 0,
    val pendingPurchaseOffers: Int = 0,
    val unreadMessages: Int = 0,
    val primaryLink: String? = null,
)

@Serializable
data class NotificationInboxItemDto(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String? = null,
    val link: String? = null,
    val read: Boolean,
    val createdAt: String,
)

@Serializable
data class UpdateSellerAnimalListingPayload(
    val category: AnimalCategory? = null,
    val type: String? = null,
    val breed: String? = null,
    val ageMonths: Int? = null,
    val quantity: Int? = null,
    val avgWeightKg: Double? = null,
    val price: Double? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrls: List<String>? = null,
)

@Serializable
data class UpdateMeatSaleRequestPayload(
    val title: String? = null,
    val meatType: String? = null,
    val animalCategory: AnimalCategory? = null,
    val cut: String? = null,
    val quantity: Double? = null,
    val pricePerKg: Double? = null,
    val packaging: String? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrls: List<String>? = null,
)

@Serializable
data class UpdateAnimalPurchasePayload(
    val title: String? = null,
    val animalCategory: AnimalCategory? = null,
    val quantity: Int? = null,
    val expectedWeight: Double? = null,
    val description: String? = null,
)

@Serializable
data class CreateSellerAnimalListingPayload(
    val category: AnimalCategory,
    val type: String,
    val breed: String? = null,
    val ageMonths: Int? = null,
    val quantity: Int,
    val avgWeightKg: Double? = null,
    val price: Double? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrls: List<String>? = null,
)

@Serializable
data class CreateSlaughterhouseListingOfferPayload(
    val pricePerKg: Double,
    val quantity: Int,
    val note: String? = null,
)

@Serializable
data class SlaughterhouseListingOfferDto(
    val offerId: Long,
    val listingId: Long,
    val listingType: String? = null,
    val listingCategory: String? = null,
    val sellerId: Long? = null,
    val sellerName: String? = null,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val pricePerKg: Double? = null,
    val quantity: Int? = null,
    val note: String? = null,
    val status: OfferStatus,
    val revisionNumber: Int? = null,
    val expiresAt: String? = null,
    val createdAt: String,
)

@Serializable
data class FavoriteMeatBuyerDto(
    val buyerId: Long,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val createdAt: String,
)

@Serializable
data class SlaughterhousePurchaseItemDto(
    val offerId: Long,
    val purchaseType: String? = null,
    val requestId: Long? = null,
    val requestTitle: String? = null,
    val listingId: Long? = null,
    val listingTitle: String? = null,
    val sellerId: Long? = null,
    val sellerName: String? = null,
    val sellerCompanyName: String? = null,
    val pricePerKg: Double? = null,
    val animalCount: Int? = null,
    val estimatedTotal: Double? = null,
    val status: OfferStatus,
    val createdAt: String,
)

@Serializable
data class SlaughterhouseSaleItemDto(
    val orderId: Long,
    val buyerId: Long? = null,
    val buyerName: String? = null,
    val meatOfferId: Long? = null,
    val saleRequestId: Long? = null,
    val saleTitle: String? = null,
    val meatType: String? = null,
    val totalPrice: Double? = null,
    val status: String? = null,
    val createdAt: String,
)

@Serializable
data class PublicUserProfileDto(
    val id: Long,
    val name: String? = null,
    val role: String? = null,
    val accountType: String? = null,
    val companyName: String? = null,
    val city: String? = null,
    val addressLine: String? = null,
    val profileImageUrl: String? = null,
    val emailVerified: Boolean = false,
    val businessVerified: Boolean = false,
)

@Serializable
data class PublicUserListingsDto(
    val meatListings: List<MeatSaleRequestDto> = emptyList(),
    val animalListings: List<SellerAnimalListingDto> = emptyList(),
)

@Serializable
data class FavoriteSlaughterhouseDto(
    val slaughterhouseId: Long,
    val slaughterhouseName: String? = null,
    val slaughterhouseCompanyName: String? = null,
    val slaughterhouseCity: String? = null,
    val slaughterhouseEmail: String? = null,
    val createdAt: String,
)

@Serializable
data class FavoriteToggleResponse(
    val isFavoritedByMe: Boolean,
)

@Serializable
data class UploadedImageResponse(
    val url: String,
)

@Serializable
data class UploadedImagesResponse(
    val urls: List<String>,
)

@Serializable
data class SlaughterhouseIncomingMeatOfferDto(
    val offerId: Long,
    val saleRequestId: Long? = null,
    val saleRequestTitle: String? = null,
    val buyerId: Long? = null,
    val buyerName: String? = null,
    val pricePerKg: Double? = null,
    val quantity: Double? = null,
    val note: String? = null,
    val status: OfferStatus,
    val createdAt: String,
)

@Serializable
data class SellerIncomingListingOfferDto(
    val offerId: Long,
    val listingId: Long? = null,
    val listingType: String? = null,
    val listingCategory: String? = null,
    val slaughterhouseId: Long? = null,
    val slaughterhouseName: String? = null,
    val pricePerKg: Double? = null,
    val quantity: Int? = null,
    val note: String? = null,
    val status: OfferStatus,
    val revisionNumber: Int? = null,
    val expiresAt: String? = null,
    val createdAt: String,
)

data class ListingReview(
    val id: String,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val timeAgo: String,
)

object ListingReviewSamples {
    fun forSeller(name: String?): List<ListingReview> = listOf(
        ListingReview("1", "Mehmet A.", 5, "Çok kaliteli ürün, tarife uygun tartı ve hızlı teslimat. Kesinlikle tekrar alacağım.", "3 gün önce"),
        ListingReview("2", "Ayşe K.", 5, "Güvenilir satıcı, belgeleri eksiksiz. Tavsiye ederim.", "1 hafta önce"),
        ListingReview("3", "Ali R.", 4, "Ürün kaliteli, fiyat biraz yüksek ama değer.", "2 hafta önce"),
    )
}
