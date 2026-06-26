package com.derdimet.mobil.model

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: Long,
    val reviewerId: Long? = null,
    val reviewerName: String? = null,
    val targetUserId: Long? = null,
    val rating: Int,
    val comment: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class UserReviewSummaryDto(
    val averageRating: Double,
    val reviewCount: Long,
)

@Serializable
data class CreateReviewRequest(
    val targetUserId: Long,
    val rating: Int,
    val comment: String? = null,
)

@Serializable
data class NotificationPreferencesDto(
    val pushOffersEnabled: Boolean,
    val pushMessagesEnabled: Boolean,
    val pushMarketingEnabled: Boolean,
)

@Serializable
data class UpdateNotificationPreferencesRequest(
    val pushOffersEnabled: Boolean? = null,
    val pushMessagesEnabled: Boolean? = null,
    val pushMarketingEnabled: Boolean? = null,
)

@Serializable
enum class DevicePlatform {
    ANDROID,
    IOS,
    WEB,
}

@Serializable
data class RegisterDeviceTokenRequest(
    val token: String,
    val platform: DevicePlatform,
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
