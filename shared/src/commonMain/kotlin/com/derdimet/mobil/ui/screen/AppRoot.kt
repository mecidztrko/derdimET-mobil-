package com.derdimet.mobil.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.viewmodel.LoginViewModel
import com.derdimet.mobil.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

private enum class AppScreen { SPLASH, LOGIN, REGISTER, MAIN }
private const val MIN_SPLASH_MS = 1200L

@Composable
fun AppRoot(
    apiService: ApiService,
    authRepository: AuthRepository,
    preferencesRepository: PreferencesRepository,
    marketService: MarketService,
    onLogoutCleanup: () -> Unit
) {
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val loginViewModel = remember {
        LoginViewModel(
            authRepository = authRepository,
            preferencesRepository = preferencesRepository
        )
    }

    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    var loggedInRole by remember { mutableStateOf<UserRole?>(null) }

    LaunchedEffect(Unit) {
        authRepository.checkAuth()
        delay(MIN_SPLASH_MS)
        val user = authRepository.fetchCurrentUser()
        if (user != null) {
            loggedInRole = user.role
            currentScreen = AppScreen.MAIN
        } else {
            currentScreen = AppScreen.LOGIN
        }
    }

    when (currentScreen) {
        AppScreen.SPLASH -> {
            Text("Yukleniyor...")
        }
        AppScreen.LOGIN -> {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                onLoginSuccess = { role ->
                    loggedInRole = role
                    currentScreen = AppScreen.MAIN
                }
            )
        }
        AppScreen.REGISTER -> {
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = { currentScreen = AppScreen.LOGIN },
                onRegisterSuccess = { role ->
                    loggedInRole = role
                    currentScreen = AppScreen.MAIN
                }
            )
        }
        AppScreen.MAIN -> {
            if (loggedInRole != null) {
                MainScreen(
                    userRole = loggedInRole!!,
                    preferencesRepository = preferencesRepository,
                    marketService = marketService,
                    onLogout = {
                        onLogoutCleanup()
                        apiService.setAuthToken(null)
                        loggedInRole = null
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
        }
    }
}
