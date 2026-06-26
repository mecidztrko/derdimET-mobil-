package com.derdimet.mobil.ui.screen

import androidx.compose.runtime.Composable
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.service.MarketService

@Composable
fun SellerProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit = {},
    onOpenMyListings: () -> Unit = {},
    onOpenPurchases: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
    onOpenSecuritySettings: () -> Unit = {},
    onOpenNotificationPreferences: () -> Unit = {},
) {
    RoleProfileScreen(
        userRole = UserRole.ANIMAL_SELLER,
        marketService = marketService,
        onLogout = onLogout,
        onSwitchRole = onSwitchRole,
        onOpenMyListings = onOpenMyListings,
        onOpenPurchases = onOpenPurchases,
        onOpenNotifications = onOpenNotifications,
        onOpenEditProfile = onOpenEditProfile,
        onOpenFavorites = onOpenFavorites,
        onOpenSecuritySettings = onOpenSecuritySettings,
        onOpenNotificationPreferences = onOpenNotificationPreferences,
    )
}
