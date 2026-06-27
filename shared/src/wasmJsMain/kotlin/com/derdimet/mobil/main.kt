package com.derdimet.mobil

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.runtime.remember
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.AuthStorage
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.repository.SettingsStorage
import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.screen.AppRoot
import com.derdimet.mobil.ui.theme.AppTheme
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("derdimET") {
        AppTheme {
            val settingsStorage = remember {
                object : SettingsStorage {
                    override fun getString(key: String): String? = window.localStorage.getItem(key)
                    override fun setString(key: String, value: String) = window.localStorage.setItem(key, value)
                    override fun remove(key: String) = window.localStorage.removeItem(key)
                }
            }
            val authStorage = remember {
                object : AuthStorage {
                    private val tokenKey = "derdimet_auth_token"
                    private val refreshKey = "derdimet_refresh_token"
                    private val expiresKey = "derdimet_token_expires_at"

                    override fun getToken(): String? = window.localStorage.getItem(tokenKey)
                    override fun setToken(token: String) = window.localStorage.setItem(tokenKey, token)
                    override fun clearToken() = window.localStorage.removeItem(tokenKey)
                    override fun getRefreshToken(): String? = window.localStorage.getItem(refreshKey)
                    override fun setRefreshToken(token: String) = window.localStorage.setItem(refreshKey, token)
                    override fun clearRefreshToken() = window.localStorage.removeItem(refreshKey)
                    override fun getTokenExpiresAtMs(): Long? {
                        val raw = window.localStorage.getItem(expiresKey) ?: return null
                        return raw.toLongOrNull()?.takeIf { it > 0L }
                    }
                    override fun setTokenExpiresAtMs(expiresAtMs: Long) =
                        window.localStorage.setItem(expiresKey, expiresAtMs.toString())
                    override fun clearTokenExpiresAtMs() = window.localStorage.removeItem(expiresKey)
                }
            }
            val apiService = remember { ApiService() }
            val preferencesRepository = remember { PreferencesRepository(settingsStorage) }
            val listingCacheRepository = remember { ListingCacheRepository(settingsStorage) }
            val authRepository = remember { AuthRepository(apiService, authStorage) }
            val marketService = remember { MarketService(apiService) }
            AppRoot(
                apiService = apiService,
                authRepository = authRepository,
                preferencesRepository = preferencesRepository,
                listingCacheRepository = listingCacheRepository,
                marketService = marketService,
                onLogoutCleanup = { authStorage.clearToken() }
            )
        }
    }
}
