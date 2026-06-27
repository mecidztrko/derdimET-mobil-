package com.derdimet.mobil.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.derdimet.mobil.model.DemoAccounts
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.navigation.AuthRoute
import com.derdimet.mobil.navigation.NavBackStack
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.service.ApiService
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.viewmodel.LoginViewModel
import com.derdimet.mobil.viewmodel.RegisterViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class RootScreen { SPLASH, AUTH, MAIN }
private const val MIN_SPLASH_MS = 1200L

@Composable
fun AppRoot(
    apiService: ApiService,
    authRepository: AuthRepository,
    preferencesRepository: PreferencesRepository,
    listingCacheRepository: ListingCacheRepository,
    marketService: MarketService,
    onLogoutCleanup: () -> Unit,
) {
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val loginViewModel = remember {
        LoginViewModel(authRepository = authRepository, preferencesRepository = preferencesRepository)
    }
    val authStack = remember { NavBackStack<AuthRoute>(AuthRoute.Login) }
    val scope = rememberCoroutineScope()

    var rootScreen by remember { mutableStateOf(RootScreen.SPLASH) }
    var loggedInRole by remember { mutableStateOf<UserRole?>(null) }
    var pendingRoleSwitch by remember { mutableStateOf<UserRole?>(null) }

    LaunchedEffect(pendingRoleSwitch) {
        val role = pendingRoleSwitch ?: return@LaunchedEffect
        pendingRoleSwitch = null
        scope.launch {
            authRepository.logout()
            onLogoutCleanup()
            apiService.setAuthToken(null)
            val success = authRepository.login(DemoAccounts.email(role), DemoAccounts.PASSWORD)
            if (success) {
                val user = authRepository.fetchCurrentUser()
                if (user != null && user.role != UserRole.ADMIN) {
                    loggedInRole = user.role
                    rootScreen = RootScreen.MAIN
                } else {
                    authRepository.logout()
                    loggedInRole = null
                    authStack.reset(AuthRoute.Login)
                    rootScreen = RootScreen.AUTH
                }
            } else {
                loggedInRole = null
                authStack.reset(AuthRoute.Login)
                rootScreen = RootScreen.AUTH
            }
        }
    }

    LaunchedEffect(Unit) {
        authRepository.checkAuth()
        delay(MIN_SPLASH_MS)
        val user = authRepository.fetchCurrentUser()
        if (user != null && user.role != UserRole.ADMIN) {
            loggedInRole = user.role
            rootScreen = RootScreen.MAIN
        } else {
            authStack.reset(AuthRoute.Login)
            rootScreen = RootScreen.AUTH
        }
    }

    when (rootScreen) {
        RootScreen.SPLASH -> SplashScreen()
        RootScreen.AUTH -> {
            when (authStack.current) {
                AuthRoute.Login -> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onNavigateToRegister = { authStack.navigate(AuthRoute.Register) },
                        onNavigateToForgotPassword = { authStack.navigate(AuthRoute.ForgotPassword) },
                        onLoginSuccess = { role ->
                            loggedInRole = role
                            rootScreen = RootScreen.MAIN
                        },
                    )
                }
                AuthRoute.ForgotPassword -> {
                    ForgotPasswordScreen(
                        authRepository = authRepository,
                        initialEmail = "",
                        onBack = { authStack.pop() },
                        onResetSuccess = { authStack.reset(AuthRoute.Login) },
                    )
                }
                AuthRoute.Register -> {
                    RegisterScreen(
                        viewModel = registerViewModel,
                        onNavigateBack = { authStack.pop() },
                        onRegisterSuccess = { role ->
                            loggedInRole = role
                            rootScreen = RootScreen.MAIN
                        },
                    )
                }
            }
        }
        RootScreen.MAIN -> {
            if (loggedInRole != null) {
                MainScreen(
                    userRole = loggedInRole!!,
                    marketService = marketService,
                    listingCacheRepository = listingCacheRepository,
                    authRepository = authRepository,
                    onLogout = {
                        scope.launch {
                            authRepository.logout()
                            onLogoutCleanup()
                            apiService.setAuthToken(null)
                            loggedInRole = null
                            authStack.reset(AuthRoute.Login)
                            rootScreen = RootScreen.AUTH
                        }
                    },
                    onSwitchRole = { pendingRoleSwitch = it },
                )
            }
        }
    }
}
