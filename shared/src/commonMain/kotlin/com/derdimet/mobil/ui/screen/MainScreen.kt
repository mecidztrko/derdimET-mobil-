package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.UserRole
import androidx.compose.foundation.layout.Arrangement
import com.derdimet.mobil.repository.AnimalCategoryFilter
import com.derdimet.mobil.repository.PreferencesRepository
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DashboardTopBar
import com.derdimet.mobil.viewmodel.SellerViewModel

sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    object Home : Tab("home", "Ana Sayfa", Icons.Default.Home)
    object Explore : Tab("explore", "Keşfet", Icons.Default.Search)

    object BuyerProfile : Tab("buyer_profile", "Profil", Icons.Default.Person)
    object BuyerSearch : Tab("buyer_search", "Arama", Icons.Default.Search)
    object BuyerOffers : Tab("buyer_offers", "Tekliflerim", Icons.Default.Campaign)

    object SellerProfile : Tab("seller_profile", "Profil", Icons.Default.Person)
    object SellerSearch : Tab("seller_search", "Arama", Icons.Default.Search)
    object SellerOffers : Tab("seller_offers", "Tekliflerim", Icons.Default.Campaign)
    object SellerCreate : Tab("seller_create", "İlan ver", Icons.Default.AddCircle)
}

@Composable
fun MainScreen(
    userRole: UserRole,
    preferencesRepository: PreferencesRepository,
    marketService: MarketService,
    onLogout: () -> Unit
) {
    val tabsForRole = remember(userRole) {
        when (userRole) {
            UserRole.MEAT_BUYER -> listOf(Tab.BuyerProfile, Tab.BuyerSearch, Tab.BuyerOffers)
            UserRole.ANIMAL_SELLER -> listOf(Tab.SellerProfile, Tab.SellerSearch, Tab.SellerOffers, Tab.SellerCreate)
            UserRole.ADMIN -> listOf(Tab.Home, Tab.Explore, Tab.SellerProfile)
            UserRole.SLAUGHTERHOUSE -> listOf(Tab.Home, Tab.Explore, Tab.SellerProfile)
        }
    }

    var selectedTab by remember(userRole) {
        mutableStateOf(tabsForRole.first())
    }
    var selectedFilter by remember { mutableStateOf(preferencesRepository.getAnimalCategoryFilter()) }
    val sellerViewModel = remember { SellerViewModel(marketService) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabsForRole.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            DashboardTopBar(
                title = when (userRole) {
                    UserRole.ADMIN -> "Yönetici"
                    UserRole.ANIMAL_SELLER -> "Satıcı Paneli"
                    UserRole.MEAT_BUYER -> "Alıcı"
                },
                subtitle = when (selectedTab) {
                    Tab.Home -> "Günlük operasyon özetiniz"
                    Tab.Explore -> "Modülleri keşfedin"
                    Tab.BuyerProfile, Tab.SellerProfile -> "Hesap ve tercihler"
                    Tab.BuyerSearch -> "İlanları keşfet ve filtrele"
                    Tab.BuyerOffers -> "Tekliflerin ve mesajların"
                    Tab.SellerSearch -> "Kesimhane ilanlarını incele"
                    Tab.SellerOffers -> "Tekliflerin ve mesajların"
                    Tab.SellerCreate -> "Yeni hayvan ilanı oluştur"
                }
            )
            if (userRole == UserRole.ADMIN) {
                AdminNotSupportedScreen(onLogout = onLogout)
            } else {
                when (selectedTab) {
                    Tab.Home -> HomeScreenByRole(
                        role = userRole,
                        sellerViewModel = sellerViewModel,
                        selectedFilter = selectedFilter
                    )
                    Tab.Explore -> ExploreScreen(userRole = userRole)
                    Tab.SellerProfile -> ProfileScreen(
                        preferencesRepository = preferencesRepository,
                        selectedFilter = selectedFilter,
                        onFilterChanged = { selectedFilter = it },
                        onLogout = onLogout
                    )
                    Tab.BuyerProfile -> BuyerProfileScreen(marketService = marketService, onLogout = onLogout)
                    Tab.BuyerSearch -> BuyerSearchScreen(marketService = marketService)
                    Tab.BuyerOffers -> BuyerMyOffersScreen(marketService = marketService)
                    Tab.SellerSearch -> SellerSearchScreen(marketService = marketService)
                    Tab.SellerOffers -> SellerOffersScreen(marketService = marketService)
                    Tab.SellerCreate -> SellerCreateListingScreen()
                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun HomeScreenByRole(
    role: UserRole,
    sellerViewModel: SellerViewModel,
    selectedFilter: AnimalCategoryFilter
) {
    when (role) {
        UserRole.ADMIN -> Unit
        UserRole.ANIMAL_SELLER -> SellerHomeScreen(sellerViewModel, selectedFilter)
        UserRole.MEAT_BUYER -> BuyerHomeScreen()
    }
}

@Composable
fun ExploreScreen(userRole: UserRole) {
    val roleLabel = when (userRole) {
        UserRole.ADMIN -> "Yönetici"
        UserRole.ANIMAL_SELLER -> "Satıcı"
        UserRole.MEAT_BUYER -> "Et Alıcı"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Keşfet", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "$roleLabel hesabı için öne çıkan modüller",
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExploreCard(
            title = "Hayvan Alış Talepleri",
            description = "Yönetici taleplerini ve ilan durumlarını hızlıca takip edin.",
            icon = Icons.Default.Inventory2
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExploreCard(
            title = "Teklif ve Pazar Akışı",
            description = "Satıcı teklifleri, fiyat karşılaştırması ve işlem durumu.",
            icon = Icons.Default.Campaign
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExploreCard(
            title = "Profil ve Filtreler",
            description = "Kategori filtresi ve hesap güvenliği ayarları.",
            icon = Icons.Default.Person
        )
    }
}

@Composable
private fun AdminNotSupportedScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Yönetici paneli mobilde desteklenmiyor.", textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
        Text(
            text = "Yönetici işlemleri için web panelini kullanın.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        Button(onClick = onLogout) { Text("Çıkış Yap") }
    }
}

@Composable
fun ProfileScreen(
    preferencesRepository: PreferencesRepository,
    selectedFilter: AnimalCategoryFilter,
    onFilterChanged: (AnimalCategoryFilter) -> Unit,
    onLogout: () -> Unit
) {
    fun setFilter(filter: AnimalCategoryFilter) {
        onFilterChanged(filter)
        preferencesRepository.setAnimalCategoryFilter(filter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Profil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Uygulama tercihlerinizi buradan yönetebilirsiniz.", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Hayvan ilanları filtresi",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Ana sayfadaki listeyi seçtiğiniz türe göre filtreler.",
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selected = selectedFilter == AnimalCategoryFilter.ALL, onClick = { setFilter(AnimalCategoryFilter.ALL) }, label = { Text("Tümü") })
                    FilterChip(selected = selectedFilter == AnimalCategoryFilter.KUCUKBAS, onClick = { setFilter(AnimalCategoryFilter.KUCUKBAS) }, label = { Text("Küçükbaş") })
                    FilterChip(selected = selectedFilter == AnimalCategoryFilter.BUYUKBAS, onClick = { setFilter(AnimalCategoryFilter.BUYUKBAS) }, label = { Text("Büyükbaş") })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Çıkış Yap")
        }
    }
}

@Composable
private fun ExploreCard(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Text(text = description, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}
