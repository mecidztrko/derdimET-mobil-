package com.derdimet.mobil.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.screen.AppRoot
import com.derdimet.mobil.ui.theme.AppTheme

@Composable
fun DerdimAndroidApp(apiBaseUrl: String) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("derdimet_prefs", Context.MODE_PRIVATE)
    }
    val settingsStorage = remember(prefs) { AndroidSettingsStorage(prefs) }
    val authStorage = remember(prefs) { AndroidAuthStorage(prefs) }
    val apiService = remember(apiBaseUrl) { ApiService(apiBaseUrl) }
    val authRepository = remember(apiService, authStorage) {
        AuthRepository(apiService, authStorage)
    }
    val preferencesRepository = remember(settingsStorage) {
        PreferencesRepository(settingsStorage)
    }
    val listingCacheRepository = remember(settingsStorage) {
        ListingCacheRepository(settingsStorage)
    }
    val marketService = remember(apiService) { MarketService(apiService) }

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    AppTheme {
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
