package com.derdimet.mobil.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    ADMIN,
    ANIMAL_SELLER,
    MEAT_BUYER,
    SLAUGHTERHOUSE
}

@Serializable
data class AuthUser(
    val id: Int,
    val email: String,
    val name: String,
    val role: UserRole,
    val phone: String? = null,
    val accountType: String,
    val companyName: String? = null,
    val taxNumber: String? = null,
    val addressLine: String? = null,
    val city: String? = null,
    val profileImageUrl: String? = null,
    val businessVerified: Boolean
)

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String
)

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val success: Boolean,
    val message: String? = null,
)

@Serializable
data class MessageResponse(val message: String)

@Serializable
data class EmailOnlyRequest(val email: String)

@Serializable
data class PasswordResetRequest(
    val email: String,
    val code: String,
    val newPassword: String,
)

@Serializable
data class UpdateProfilePayload(
    val name: String? = null,
    val phone: String? = null,
    val companyName: String? = null,
    val taxNumber: String? = null,
    val addressLine: String? = null,
    val city: String? = null,
    val contactSecondaryName: String? = null,
    val contactSecondaryPhone: String? = null,
    val profileImageUrl: String? = null,
)
