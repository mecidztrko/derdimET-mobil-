package com.derdimet.mobil.model

import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val id: Int,
    val email: String,
    val name: String,
    val role: String,
    val phone: String? = null,
    val accountType: String,
    val companyName: String? = null,
    val taxNumber: String? = null,
    val addressLine: String? = null,
    val city: String? = null,
    val contactSecondaryName: String? = null,
    val contactSecondaryPhone: String? = null,
    val profileImageUrl: String? = null,
    val emailVerified: Boolean = false,
    val businessVerified: Boolean,
    val businessVerificationStatus: String? = null,
    val businessVerificationNote: String? = null,
)

fun MeResponse.toAuthUser(): AuthUser? {
    val userRole = try {
        UserRole.valueOf(role)
    } catch (e: Exception) {
        null
    }

    return userRole?.let {
        AuthUser(
            id = id,
            email = email,
            name = name,
            role = it,
            phone = phone,
            accountType = accountType,
            companyName = companyName,
            taxNumber = taxNumber,
            addressLine = addressLine,
            city = city,
            profileImageUrl = profileImageUrl,
            businessVerified = businessVerified
        )
    }
}
