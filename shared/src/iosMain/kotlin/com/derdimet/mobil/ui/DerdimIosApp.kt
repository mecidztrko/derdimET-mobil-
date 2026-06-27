package com.derdimet.mobil.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.screen.AppRoot
import com.derdimet.mobil.ui.theme.AppTheme
import platform.UIKit.UIViewController

fun MainViewController(apiBaseUrl: String = "http://localhost:8081"): UIViewController =
    ComposeUIViewController {
        DerdimIosApp(apiBaseUrl = apiBaseUrl)
    }

@Composable
fun DerdimIosApp(apiBaseUrl: String) {
    val settingsStorage = remember { IosSettingsStorage() }
    val authStorage = remember { IosAuthStorage() }
    val apiService = remember(apiBaseUrl) { ApiService(apiBaseUrl) }
    val authRepository = remember(apiService, authStorage) { AuthRepository(apiService, authStorage) }
    val preferencesRepository = remember(settingsStorage) { PreferencesRepository(settingsStorage) }
    val listingCacheRepository = remember(settingsStorage) { ListingCacheRepository(settingsStorage) }
    val marketService = remember(apiService) { MarketService(apiService) }

    AppTheme {
        AppRoot(
            apiService = apiService,
            authRepository = authRepository,
            preferencesRepository = preferencesRepository,
            listingCacheRepository = listingCacheRepository,
            marketService = marketService,
            onLogoutCleanup = { authStorage.clearToken() },
        )
    }
}
