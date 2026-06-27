package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.navigation.NavBackStack
import com.derdimet.mobil.navigation.ProfileRoute
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.AppBottomBar
import com.derdimet.mobil.ui.components.AppNavTab
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.viewmodel.BuyerSearchViewModel
import com.derdimet.mobil.viewmodel.MessagesInboxViewModel
import com.derdimet.mobil.viewmodel.SellerSearchViewModel
import com.derdimet.mobil.viewmodel.SlaughterhouseSearchViewModel

@Composable
fun MainScreen(
    userRole: UserRole,
    marketService: MarketService,
    listingCacheRepository: ListingCacheRepository,
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit = {},
) {
    val tabsForRole = remember(userRole) { navTabsForRole(userRole) }
    var selectedTab by remember(userRole) { mutableStateOf(AppNavTab.Search) }
    var offerBadge by remember { mutableIntStateOf(0) }
    var messageBadge by remember { mutableIntStateOf(0) }
    var inboxRefreshKey by remember { mutableIntStateOf(0) }
    val profileStack = remember { NavBackStack<ProfileRoute>(ProfileRoute.None) }
    val buyerSearchViewModel = remember(marketService, listingCacheRepository) {
        BuyerSearchViewModel(marketService, listingCacheRepository)
    }
    val slaughterhouseSearchViewModel = remember(marketService, listingCacheRepository) {
        SlaughterhouseSearchViewModel(marketService, listingCacheRepository)
    }
    val sellerSearchViewModel = remember(marketService, listingCacheRepository) {
        SellerSearchViewModel(marketService, listingCacheRepository)
    }
    val messagesViewModel = remember(marketService, listingCacheRepository) {
        MessagesInboxViewModel(marketService, listingCacheRepository)
    }

    LaunchedEffect(inboxRefreshKey) {
        if (inboxRefreshKey > 0) messagesViewModel.load()
    }

    LaunchedEffect(userRole, selectedTab) {
        val notifRes = marketService.fetchNotificationSummary()
        if (notifRes.success && notifRes.data != null) {
            val n = notifRes.data
            messageBadge = n.unreadMessages
            offerBadge = n.pendingOffers + n.pendingIncoming + n.pendingPurchaseOffers
        } else {
            val convRes = marketService.fetchConversations()
            if (convRes.success) {
                messageBadge = convRes.data?.sumOf { it.unreadCount } ?: 0
            }
            val pending = when (userRole) {
                UserRole.MEAT_BUYER -> marketService.fetchMyBuyerMeatOffers().data
                    ?.count { it.status == OfferStatus.PENDING } ?: 0
                UserRole.ANIMAL_SELLER -> marketService.fetchSellerIncomingListingOffers().data
                    ?.count { it.status == OfferStatus.PENDING } ?: 0
                UserRole.SLAUGHTERHOUSE -> marketService.fetchSlaughterhouseIncomingMeatOffers().data
                    ?.count { it.status == OfferStatus.PENDING } ?: 0
                UserRole.ADMIN -> 0
            }
            offerBadge = pending
        }
    }

    Scaffold(
        bottomBar = {
            if (userRole != UserRole.ADMIN) {
                AppBottomBar(
                    tabs = tabsForRole,
                    selected = selectedTab,
                    onSelect = { tab ->
                        selectedTab = tab
                        if (tab == AppNavTab.Messages) inboxRefreshKey++
                    },
                    offerBadge = offerBadge,
                    messageBadge = messageBadge,
                )
            }
        },
        containerColor = FigmaStyle.ScreenBg,
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (userRole == UserRole.ADMIN) {
                AdminNotSupportedScreen(onLogout = onLogout)
            } else {
                when (selectedTab) {
                    AppNavTab.Search -> SearchScreenByRole(
                        userRole,
                        marketService,
                        buyerSearchViewModel,
                        slaughterhouseSearchViewModel,
                        sellerSearchViewModel,
                    )
                    AppNavTab.Offers -> OffersScreenByRole(userRole, marketService)
                    AppNavTab.Messages -> MessagesInboxScreen(
                        viewModel = messagesViewModel,
                        marketService = marketService,
                        refreshKey = inboxRefreshKey,
                    )
                    AppNavTab.Profile -> Box(Modifier.fillMaxSize()) {
                        ProfileScreenByRole(
                            userRole = userRole,
                            marketService = marketService,
                            onLogout = onLogout,
                            onSwitchRole = onSwitchRole,
                            onOpenMyListings = { profileStack.navigate(ProfileRoute.MyListings) },
                            onOpenPurchases = { profileStack.navigate(ProfileRoute.Purchases) },
                            onOpenNotifications = { profileStack.navigate(ProfileRoute.Notifications) },
                            onOpenEditProfile = { profileStack.navigate(ProfileRoute.EditProfile) },
                            onOpenFavorites = { profileStack.navigate(ProfileRoute.Favorites) },
                            onOpenSecuritySettings = { profileStack.navigate(ProfileRoute.ChangePassword) },
                            onOpenNotificationPreferences = { profileStack.navigate(ProfileRoute.NotificationPreferences) },
                            onOpenBusinessVerification = { profileStack.navigate(ProfileRoute.BusinessVerification) },
                        )
                        when (profileStack.current) {
                            ProfileRoute.None -> Unit
                            ProfileRoute.MyListings -> MyListingsScreen(userRole, marketService) { profileStack.pop() }
                            ProfileRoute.Purchases -> MyPurchasesScreen(userRole, marketService) { profileStack.pop() }
                            ProfileRoute.Notifications -> NotificationsScreen(marketService) { profileStack.pop() }
                            ProfileRoute.EditProfile -> EditProfileScreen(
                                marketService,
                                authRepository,
                                onBack = { profileStack.pop() },
                                onSaved = { profileStack.pop() },
                            )
                            ProfileRoute.ChangePassword -> ChangePasswordScreen(authRepository, onBack = { profileStack.pop() })
                            ProfileRoute.NotificationPreferences -> NotificationPreferencesScreen(marketService, onBack = { profileStack.pop() })
                            ProfileRoute.BusinessVerification -> BusinessVerificationScreen(marketService, onBack = { profileStack.pop() })
                            ProfileRoute.Favorites -> when (userRole) {
                                UserRole.ANIMAL_SELLER -> SellerFavoritesScreen(marketService) { profileStack.pop() }
                                UserRole.SLAUGHTERHOUSE -> SlaughterhouseFavoritesScreen(marketService) { profileStack.pop() }
                                else -> BuyerFavoritesScreen(marketService) { profileStack.pop() }
                            }
                        }
                    }
                    AppNavTab.Create -> CreateScreenByRole(userRole, marketService)
                }
            }
        }
    }
}

private fun navTabsForRole(role: UserRole): List<AppNavTab> = when (role) {
    UserRole.MEAT_BUYER -> listOf(
        AppNavTab.Search,
        AppNavTab.Offers,
        AppNavTab.Messages,
        AppNavTab.Profile,
    )
    UserRole.ANIMAL_SELLER, UserRole.SLAUGHTERHOUSE -> listOf(
        AppNavTab.Search,
        AppNavTab.Offers,
        AppNavTab.Create,
        AppNavTab.Messages,
        AppNavTab.Profile,
    )
    UserRole.ADMIN -> emptyList()
}

@Composable
private fun SearchScreenByRole(
    role: UserRole,
    marketService: MarketService,
    buyerSearchViewModel: BuyerSearchViewModel,
    slaughterhouseSearchViewModel: SlaughterhouseSearchViewModel,
    sellerSearchViewModel: SellerSearchViewModel,
) {
    when (role) {
        UserRole.MEAT_BUYER -> BuyerSearchScreen(viewModel = buyerSearchViewModel, marketService = marketService)
        UserRole.ANIMAL_SELLER -> SellerSearchScreen(viewModel = sellerSearchViewModel, marketService = marketService)
        UserRole.SLAUGHTERHOUSE -> SlaughterhouseSearchScreen(
            viewModel = slaughterhouseSearchViewModel,
            marketService = marketService,
        )
        UserRole.ADMIN -> Unit
    }
}

@Composable
private fun OffersScreenByRole(role: UserRole, marketService: MarketService) {
    when (role) {
        UserRole.MEAT_BUYER -> BuyerMyOffersScreen(marketService = marketService)
        UserRole.ANIMAL_SELLER -> SellerOffersScreen(marketService = marketService)
        UserRole.SLAUGHTERHOUSE -> SlaughterhouseOffersScreen(marketService = marketService)
        UserRole.ADMIN -> Unit
    }
}

@Composable
private fun ProfileScreenByRole(
    userRole: UserRole,
    marketService: MarketService,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    onOpenMyListings: () -> Unit,
    onOpenPurchases: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenFavorites: () -> Unit = {},
    onOpenSecuritySettings: () -> Unit = {},
    onOpenNotificationPreferences: () -> Unit = {},
    onOpenBusinessVerification: () -> Unit = {},
) {
    when (userRole) {
        UserRole.MEAT_BUYER -> BuyerProfileScreen(
            marketService, onLogout, onSwitchRole, onOpenPurchases, onOpenNotifications,
            onOpenEditProfile, onOpenFavorites, onOpenSecuritySettings, onOpenNotificationPreferences,
            onOpenBusinessVerification,
        )
        UserRole.ANIMAL_SELLER -> SellerProfileScreen(
            marketService, onLogout, onSwitchRole, onOpenMyListings, onOpenPurchases, onOpenNotifications,
            onOpenEditProfile, onOpenFavorites, onOpenSecuritySettings, onOpenNotificationPreferences,
            onOpenBusinessVerification,
        )
        UserRole.SLAUGHTERHOUSE -> SlaughterhouseProfileScreen(
            marketService, onLogout, onSwitchRole, onOpenMyListings, onOpenPurchases, onOpenNotifications,
            onOpenEditProfile, onOpenFavorites, onOpenSecuritySettings, onOpenNotificationPreferences,
            onOpenBusinessVerification,
        )
        UserRole.ADMIN -> Unit
    }
}

@Composable
private fun CreateScreenByRole(role: UserRole, marketService: MarketService) {
    when (role) {
        UserRole.ANIMAL_SELLER -> SellerCreateListingScreen(marketService = marketService)
        UserRole.SLAUGHTERHOUSE -> SlaughterhouseCreateHubScreen(marketService = marketService)
        else -> Unit
    }
}

@Composable
private fun AdminNotSupportedScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Yönetici paneli mobilde desteklenmiyor.",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Yönetici işlemleri için web panelini kullanın.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        Button(onClick = onLogout) { Text("Çıkış Yap") }
    }
}
