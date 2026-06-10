package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.repository.AnimalCategoryFilter
import com.derdimet.mobil.repository.AuthRepository
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.AppBottomBar
import com.derdimet.mobil.ui.components.AppNavTab
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.viewmodel.SellerViewModel

private sealed class ProfileOverlay {
    data object None : ProfileOverlay()
    data object MyListings : ProfileOverlay()
    data object Purchases : ProfileOverlay()
    data object Notifications : ProfileOverlay()
    data object EditProfile : ProfileOverlay()
    data object Favorites : ProfileOverlay()
}

@Composable
fun MainScreen(
    userRole: UserRole,
    preferencesRepository: PreferencesRepository,
    marketService: MarketService,
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit = {},
) {
    val tabsForRole = remember(userRole) { navTabsForRole(userRole) }
    var selectedTab by remember(userRole) { mutableStateOf(AppNavTab.Search) }
    var offerBadge by remember { mutableIntStateOf(0) }
    var messageBadge by remember { mutableIntStateOf(0) }
    var inboxRefreshKey by remember { mutableIntStateOf(0) }
    var profileOverlay by remember { mutableStateOf<ProfileOverlay>(ProfileOverlay.None) }
    var selectedFilter by remember { mutableStateOf(preferencesRepository.getAnimalCategoryFilter()) }
    val sellerViewModel = remember { SellerViewModel(marketService) }

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
                    AppNavTab.Search -> SearchScreenByRole(userRole, marketService)
                    AppNavTab.Offers -> OffersScreenByRole(userRole, marketService)
                    AppNavTab.Messages -> MessagesInboxScreen(
                        marketService = marketService,
                        refreshKey = inboxRefreshKey,
                    )
                    AppNavTab.Profile -> Box(Modifier.fillMaxSize()) {
                        ProfileScreenByRole(
                            userRole = userRole,
                            marketService = marketService,
                            onLogout = onLogout,
                            onSwitchRole = onSwitchRole,
                            onOpenMyListings = { profileOverlay = ProfileOverlay.MyListings },
                            onOpenPurchases = { profileOverlay = ProfileOverlay.Purchases },
                            onOpenNotifications = { profileOverlay = ProfileOverlay.Notifications },
                            onOpenEditProfile = { profileOverlay = ProfileOverlay.EditProfile },
                            onOpenFavorites = { profileOverlay = ProfileOverlay.Favorites },
                        )
                        when (profileOverlay) {
                            ProfileOverlay.None -> Unit
                            ProfileOverlay.MyListings -> MyListingsScreen(userRole, marketService) { profileOverlay = ProfileOverlay.None }
                            ProfileOverlay.Purchases -> MyPurchasesScreen(userRole, marketService) { profileOverlay = ProfileOverlay.None }
                            ProfileOverlay.Notifications -> NotificationsScreen(marketService) { profileOverlay = ProfileOverlay.None }
                            ProfileOverlay.EditProfile -> EditProfileScreen(marketService, authRepository, onBack = { profileOverlay = ProfileOverlay.None }, onSaved = { profileOverlay = ProfileOverlay.None })
                            ProfileOverlay.Favorites -> when (userRole) {
                                UserRole.ANIMAL_SELLER -> SellerFavoritesScreen(marketService) { profileOverlay = ProfileOverlay.None }
                                UserRole.SLAUGHTERHOUSE -> SlaughterhouseFavoritesScreen(marketService) { profileOverlay = ProfileOverlay.None }
                                else -> BuyerFavoritesScreen(marketService) { profileOverlay = ProfileOverlay.None }
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
private fun SearchScreenByRole(role: UserRole, marketService: MarketService) {
    when (role) {
        UserRole.MEAT_BUYER -> BuyerSearchScreen(marketService = marketService)
        UserRole.ANIMAL_SELLER -> SellerSearchScreen(marketService = marketService)
        UserRole.SLAUGHTERHOUSE -> SlaughterhouseSearchScreen(marketService = marketService)
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
) {
    when (userRole) {
        UserRole.MEAT_BUYER -> BuyerProfileScreen(marketService, onLogout, onSwitchRole, onOpenPurchases, onOpenNotifications, onOpenEditProfile, onOpenFavorites)
        UserRole.ANIMAL_SELLER -> SellerProfileScreen(marketService, onLogout, onSwitchRole, onOpenMyListings, onOpenPurchases, onOpenNotifications, onOpenEditProfile, onOpenFavorites)
        UserRole.SLAUGHTERHOUSE -> SlaughterhouseProfileScreen(marketService, onLogout, onSwitchRole, onOpenMyListings, onOpenPurchases, onOpenNotifications, onOpenEditProfile, onOpenFavorites)
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
fun HomeScreenByRole(
    role: UserRole,
    sellerViewModel: SellerViewModel,
    selectedFilter: AnimalCategoryFilter,
) {
    when (role) {
        UserRole.ADMIN -> Unit
        UserRole.ANIMAL_SELLER -> SellerHomeScreen(sellerViewModel, selectedFilter)
        UserRole.MEAT_BUYER -> BuyerHomeScreen()
        UserRole.SLAUGHTERHOUSE -> Unit
    }
}

@Composable
fun ExploreScreen(userRole: UserRole) {
    val roleLabel = when (userRole) {
        UserRole.ADMIN -> "Yönetici"
        UserRole.ANIMAL_SELLER -> "Satıcı"
        UserRole.MEAT_BUYER -> "Et Alıcı"
        UserRole.SLAUGHTERHOUSE -> "Kesimhane"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(text = "Keşfet", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "$roleLabel hesabı için öne çıkan modüller",
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExploreCard(
            title = "Hayvan Alış Talepleri",
            description = "Yönetici taleplerini ve ilan durumlarını hızlıca takip edin.",
            icon = Icons.Default.Inventory2,
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExploreCard(
            title = "Teklif ve Pazar Akışı",
            description = "Satıcı teklifleri, fiyat karşılaştırması ve işlem durumu.",
            icon = Icons.Default.Campaign,
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExploreCard(
            title = "Profil ve Filtreler",
            description = "Kategori filtresi ve hesap güvenliği ayarları.",
            icon = Icons.Default.Person,
        )
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

@Composable
private fun ExploreCard(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(text = description, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}
