package com.derdimet.mobil.navigation

sealed class AuthRoute {
    data object Login : AuthRoute()
    data object Register : AuthRoute()
    data object ForgotPassword : AuthRoute()
}
