package com.derdimet.mobil.navigation

sealed class ProfileRoute {
    data object None : ProfileRoute()
    data object MyListings : ProfileRoute()
    data object Purchases : ProfileRoute()
    data object Notifications : ProfileRoute()
    data object EditProfile : ProfileRoute()
    data object Favorites : ProfileRoute()
    data object ChangePassword : ProfileRoute()
    data object NotificationPreferences : ProfileRoute()
    data object BusinessVerification : ProfileRoute()
}
