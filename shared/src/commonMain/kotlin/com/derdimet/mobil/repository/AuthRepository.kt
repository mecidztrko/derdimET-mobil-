package com.derdimet.mobil.repository

import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.model.AuthUser
import com.derdimet.mobil.model.toAuthUser

interface AuthStorage {
    fun getToken(): String?
    fun setToken(token: String)
    fun clearToken()
}

class AuthRepository(
    private val apiService: ApiService,
    private val authStorage: AuthStorage
) {
    private val KEY = "derdimet_auth_token"

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val response = apiService.login(email, password)
            if (response.success) {
                response.data.token.let { 
                    authStorage.setToken(it)
                    apiService.setAuthToken(it)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        authStorage.clearToken()
        apiService.setAuthToken(null)
    }

    fun checkAuth() {
        val token = authStorage.getToken()
        apiService.setAuthToken(token)
    }

    suspend fun fetchCurrentUser(): AuthUser? {
        return try {
            val response = apiService.me()
            if (response.success) response.data.toAuthUser() else null
        } catch (e: Exception) {
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
