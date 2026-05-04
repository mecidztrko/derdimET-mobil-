package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.derdimet.mobil.viewmodel.AdminViewModel
import com.derdimet.mobil.viewmodel.SellerViewModel

sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    object Home : Tab("home", "Ana Sayfa", Icons.Default.Home)
    object Explore : Tab("explore", "Keşfet", Icons.Default.Search)
    object Profile : Tab("profile", "Profil", Icons.Default.Person)
}

@Composable
fun MainScreen(
    userRole: UserRole,
    preferencesRepository: PreferencesRepository,
    marketService: MarketService,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf<Tab>(Tab.Home) }
    var selectedFilter by remember { mutableStateOf(preferencesRepository.getAnimalCategoryFilter()) }
    val adminViewModel = remember { AdminViewModel(marketService) }
    val sellerViewModel = remember { SellerViewModel(marketService) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val tabs = listOf(Tab.Home, Tab.Explore, Tab.Profile)
                tabs.forEach { tab ->
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
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                Tab.Home -> HomeScreenByRole(
                    role = userRole,
                    adminViewModel = adminViewModel,
                    sellerViewModel = sellerViewModel,
                    selectedFilter = selectedFilter
                )
                Tab.Explore -> ExploreScreen()
                Tab.Profile -> ProfileScreen(
                    preferencesRepository = preferencesRepository,
                    selectedFilter = selectedFilter,
                    onFilterChanged = { selectedFilter = it },
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun HomeScreenByRole(
    role: UserRole,
    adminViewModel: AdminViewModel,
    sellerViewModel: SellerViewModel,
    selectedFilter: AnimalCategoryFilter
) {
    when (role) {
        UserRole.ADMIN -> AdminHomeScreen(adminViewModel)
        UserRole.ANIMAL_SELLER -> SellerHomeScreen(sellerViewModel, selectedFilter)
        UserRole.MEAT_BUYER -> BuyerHomeScreen()
    }
}

@Composable
fun ExploreScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Keşfet", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Yeni ilanları ve fırsatları buradan takip edebilirsiniz.")
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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(32.dp))
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
