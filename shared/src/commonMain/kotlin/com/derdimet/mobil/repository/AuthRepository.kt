package com.derdimet.mobil.repository

import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.model.AuthUser
import com.derdimet.mobil.model.MeResponse
import com.derdimet.mobil.model.UpdateProfilePayload
import com.derdimet.mobil.model.toAuthUser
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.utils.io.errors.IOException
import kotlinx.datetime.Clock

interface AuthStorage {
    fun getToken(): String?
    fun setToken(token: String)
    fun clearToken()
    fun getRefreshToken(): String?
    fun setRefreshToken(token: String)
    fun clearRefreshToken()
    fun getTokenExpiresAtMs(): Long?
    fun setTokenExpiresAtMs(expiresAtMs: Long)
    fun clearTokenExpiresAtMs()
}

class AuthRepository(
    private val apiService: ApiService,
    private val authStorage: AuthStorage
) {
    private val KEY = "derdimet_auth_token"
    private val REFRESH_KEY = "derdimet_refresh_token"
    private val EXPIRES_KEY = "derdimet_token_expires_at"
    private var lastLoginError: String? = null

    fun consumeLastLoginError(): String? {
        val value = lastLoginError
        lastLoginError = null
        return value
    }

    suspend fun login(email: String, password: String): Boolean {
        lastLoginError = null
        return try {
            val response = apiService.login(email, password)
            val token = response.data?.token
            if (response.success && !token.isNullOrBlank()) {
                authStorage.setToken(token)
                response.data?.refreshToken?.let { authStorage.setRefreshToken(it) }
                response.data?.expiresInSeconds?.let { seconds ->
                    authStorage.setTokenExpiresAtMs(Clock.System.now().toEpochMilliseconds() + seconds * 1000)
                }
                apiService.setAuthToken(token)
                true
            } else {
                lastLoginError = response.message ?: "Giriş başarısız"
                false
            }
        } catch (e: ClientRequestException) {
            lastLoginError = if (e.response.status.value == 401) {
                "E-posta veya şifre yanlış."
            } else {
                "İstek hatası: ${e.response.status.value}"
            }
            false
        } catch (e: ServerResponseException) {
            lastLoginError = "Sunucu hatası: ${e.response.status.value}"
            false
        } catch (e: HttpRequestTimeoutException) {
            lastLoginError = "Sunucuya zamanında ulaşılamadı."
            false
        } catch (e: IOException) {
            lastLoginError = "Sunucuya bağlanılamadı. API adresini kontrol edin."
            false
        } catch (e: Exception) {
            lastLoginError = e.message ?: "Bir hata oluştu"
            false
        }
    }

    suspend fun logout() {
        val refresh = authStorage.getRefreshToken()
        if (!refresh.isNullOrBlank()) {
            runCatching { apiService.logout(refresh) }
        }
        authStorage.clearToken()
        authStorage.clearRefreshToken()
        authStorage.clearTokenExpiresAtMs()
        apiService.setAuthToken(null)
    }

    suspend fun refreshAccessToken(): Boolean {
        val refresh = authStorage.getRefreshToken() ?: return false
        return try {
            val response = apiService.refresh(refresh)
            val data = response.data ?: return false
            val token = data.token
            if (!response.success || token.isBlank()) return false
            authStorage.setToken(token)
            data.refreshToken?.let { authStorage.setRefreshToken(it) }
            data.expiresInSeconds?.let { seconds ->
                authStorage.setTokenExpiresAtMs(Clock.System.now().toEpochMilliseconds() + seconds * 1000)
            }
            apiService.setAuthToken(token)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun checkAuth() {
        val token = authStorage.getToken()
        apiService.setAuthToken(token)
        apiService.tokenRefresher = { refreshAccessToken() }
    }

    suspend fun fetchCurrentUser(): AuthUser? {
        return try {
            val response = apiService.me()
            if (response.success) response.data?.toAuthUser() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun forgotPassword(email: String): String? {
        return try {
            val response = apiService.forgotPassword(email)
            if (response.success) null else response.message ?: "İşlem başarısız"
        } catch (e: Exception) {
            e.message ?: "Sunucuya bağlanılamadı"
        }
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): String? {
        return try {
            val response = apiService.resetPassword(email, code, newPassword)
            if (response.success) null else response.message ?: "Şifre güncellenemedi"
        } catch (e: Exception) {
            e.message ?: "Sunucuya bağlanılamadı"
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): String? {
        return try {
            val response = apiService.changePassword(currentPassword, newPassword)
            if (response.success) null else response.message ?: "Şifre güncellenemedi"
        } catch (e: Exception) {
            e.message ?: "Sunucuya bağlanılamadı"
        }
    }

    suspend fun updateProfile(payload: UpdateProfilePayload): MeResponse? {
        return try {
            val response = apiService.patch<MeResponse>("/api/me", payload)
            if (response.success) response.data else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String,
        accountType: String,
        phone: String? = null,
        companyName: String? = null,
        taxNumber: String? = null,
        addressLine: String? = null,
        city: String? = null
    ): Boolean {
        val payload = mutableMapOf<String, Any?>(
            "email" to email.trim(),
            "password" to password,
            "name" to name.trim(),
            "role" to role,
            "accountType" to accountType
        )
        if (!phone.isNullOrBlank()) payload["phone"] = phone.trim()
        if (!companyName.isNullOrBlank()) payload["companyName"] = companyName.trim()
        if (!taxNumber.isNullOrBlank()) payload["taxNumber"] = taxNumber.trim()
        if (!addressLine.isNullOrBlank()) payload["addressLine"] = addressLine.trim()
        if (!city.isNullOrBlank()) payload["city"] = city.trim()
        return try {
            apiService.register(payload).success
        } catch (e: Exception) {
            false
        }
    }
}
