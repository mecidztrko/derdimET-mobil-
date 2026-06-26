package com.derdimet.mobil.ui.screen

import androidx.compose.runtime.Composable
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.service.MarketService

@Composable
fun BuyerProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit = {},
    onOpenPurchases: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
    onOpenSecuritySettings: () -> Unit = {},
    onOpenNotificationPreferences: () -> Unit = {},
) {
    RoleProfileScreen(
        userRole = UserRole.MEAT_BUYER,
        marketService = marketService,
        onLogout = onLogout,
        onSwitchRole = onSwitchRole,
        onOpenPurchases = onOpenPurchases,
        onOpenNotifications = onOpenNotifications,
        onOpenEditProfile = onOpenEditProfile,
        onOpenFavorites = onOpenFavorites,
        onOpenSecuritySettings = onOpenSecuritySettings,
        onOpenNotificationPreferences = onOpenNotificationPreferences,
    )
}
