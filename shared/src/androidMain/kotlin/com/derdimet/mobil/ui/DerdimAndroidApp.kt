package com.derdimet.mobil.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.derdimet.mobil.repository.AuthRepository
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
    val marketService = remember(apiService) { MarketService(apiService) }

    AppTheme {
        AppRoot(
            apiService = apiService,
            authRepository = authRepository,
            preferencesRepository = preferencesRepository,
            marketService = marketService,
            onLogoutCleanup = { authStorage.clearToken() }
        )
    }
}
