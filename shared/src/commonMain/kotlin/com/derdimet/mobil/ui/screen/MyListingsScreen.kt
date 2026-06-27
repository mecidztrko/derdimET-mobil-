package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.model.RequestStatus
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.model.UserRole
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber

@Composable
fun MyListingsScreen(
    userRole: UserRole,
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var sellerListings by remember { mutableStateOf<List<SellerAnimalListingDto>>(emptyList()) }
    var meatRequests by remember { mutableStateOf<List<MeatSaleRequestDto>>(emptyList()) }
    var purchaseRequests by remember { mutableStateOf<List<AnimalPurchaseRequestDto>>(emptyList()) }
    var shTab by remember { mutableStateOf("meat") }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var editingListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }
    val scope = rememberCoroutineScope()

    val listingToEdit = editingListing
    if (listingToEdit != null) {
        SellerEditListingScreen(
            listing = listingToEdit,
            marketService = marketService,
            onBack = { editingListing = null },
            onSaved = {
                editingListing = null
                actionMessage = "İlan güncellendi."
                refreshKey++
            },
        )
        return
    }

    LaunchedEffect(userRole, refreshKey) {
        loading = true
        error = null
        when (userRole) {
            UserRole.ANIMAL_SELLER -> {
                val res = marketService.fetchMySellerAnimalListings()
                if (res.success) sellerListings = res.data ?: emptyList() else error = res.message
            }
            UserRole.SLAUGHTERHOUSE -> {
                val meat = marketService.fetchMySlaughterhouseMeatSaleRequests()
                val purchase = marketService.fetchMySlaughterhouseAnimalPurchaseRequests()
                if (meat.success) meatRequests = meat.data ?: emptyList()
                if (purchase.success) purchaseRequests = purchase.data ?: emptyList()
                if (!meat.success && !purchase.success) error = meat.message ?: purchase.message
            }
            else -> Unit
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "İlanlarım", showBack = true, onBack = onBack)
        DerdimListScreenBody(
            header = {
                actionMessage?.let { Text(it, fontSize = 12.sp, color = Color(0xFF166534)) }
                if (userRole == UserRole.SLAUGHTERHOUSE) {
                    DerdimFilterTabs(
                        tabs = listOf(
                            Triple("meat", "Et ilanları", meatRequests.size),
                            Triple("purchase", "Alım talepleri", purchaseRequests.size),
                        ),
                        selectedKey = shTab,
                        onSelect = { shTab = it },
                    )
                }
            },
            content = {
                val isEmpty = when (userRole) {
                    UserRole.ANIMAL_SELLER -> sellerListings.isEmpty()
                    UserRole.SLAUGHTERHOUSE -> {
                        if (shTab == "meat") meatRequests.isEmpty() else purchaseRequests.isEmpty()
                    }
                    else -> true
                }
                DerdimScreenState(
                    loading = loading,
                    error = error,
                    empty = isEmpty,
                    emptyTitle = "Henüz ilanınız yok",
                    emptyMessage = if (userRole == UserRole.SLAUGHTERHOUSE) "Bu kategoride ilan bulunmuyor." else "Yeni ilan oluşturarak başlayabilirsiniz.",
                    onRetry = { refreshKey++ },
                ) {
                    when (userRole) {
                        UserRole.ANIMAL_SELLER -> {
                            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(sellerListings, key = { it.id }) { listing ->
                                    ListingManageCard(
                                        title = "${listing.type} · ${listing.quantity} adet",
                                        subtitle = listing.location ?: listing.sellerCity ?: "—",
                                        price = listing.price?.let { "${formatNumber(it)} ₺" },
                                        status = listing.status,
                                        onClose = {
                                            scope.launch {
                                                marketService.closeSellerAnimalListing(listing.id)
                                                actionMessage = "İlan kapatıldı."
                                                refreshKey++
                                            }
                                        },
                                        onReopen = {
                                            scope.launch {
                                                marketService.reopenSellerAnimalListing(listing.id)
                                                actionMessage = "İlan yeniden açıldı."
                                                refreshKey++
                                            }
                                        },
                                        onEdit = if (listing.status == RequestStatus.OPEN) {
                                            { editingListing = listing }
                                        } else null,
                                    )
                                }
                            }
                        }
                        UserRole.SLAUGHTERHOUSE -> {
                            if (shTab == "meat") {
                                LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(meatRequests, key = { it.id }) { item ->
                                        ListingManageCard(
                                            title = item.title,
                                            subtitle = item.meatType,
                                            price = item.pricePerKg?.let { "${formatNumber(it)} ₺/kg" },
                                            status = item.status,
                                            onClose = {
                                                scope.launch {
                                                    marketService.closeMeatSaleRequest(item.id)
                                                    actionMessage = "İlan kapatıldı."
                                                    refreshKey++
                                                }
                                            },
                                            onReopen = {
                                                scope.launch {
                                                    marketService.reopenMeatSaleRequest(item.id)
                                                    actionMessage = "İlan yeniden açıldı."
                                                    refreshKey++
                                                }
                                            },
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(purchaseRequests, key = { it.id }) { item ->
                                        ListingManageCard(
                                            title = item.title,
                                            subtitle = item.animalCategory?.name ?: "—",
                                            price = item.expectedWeight?.let { "${formatNumber(it)} kg" },
                                            status = item.status,
                                            onClose = {
                                                scope.launch {
                                                    marketService.closeAnimalPurchaseRequest(item.id.toLong())
                                                    actionMessage = "Talep kapatıldı."
                                                    refreshKey++
                                                }
                                            },
                                            onReopen = {
                                                scope.launch {
                                                    marketService.reopenAnimalPurchaseRequest(item.id.toLong())
                                                    actionMessage = "Talep yeniden açıldı."
                                                    refreshKey++
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        else -> Text("Bu rol için ilan yönetimi yok.", color = DerdimColors.MutedForeground)
                    }
                }
            },
        )
    }
}

@Composable
internal fun ListingManageCard(
    title: String,
    subtitle: String,
    price: String?,
    status: RequestStatus,
    onClose: () -> Unit,
    onReopen: () -> Unit,
    onEdit: (() -> Unit)? = null,
) {
    Column(
        Modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(subtitle, fontSize = 12.sp, color = DerdimColors.MutedForeground)
        price?.let { Text(it, fontWeight = FontWeight.Bold, color = DerdimColors.Primary, fontSize = 13.sp) }
        Text(
            if (status == RequestStatus.OPEN) "Açık" else "Kapalı",
            fontSize = 11.sp,
            color = if (status == RequestStatus.OPEN) DerdimColors.Success else DerdimColors.MutedForeground,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (status == RequestStatus.OPEN) {
                onEdit?.let { edit ->
                    FigmaSecondaryButton("Düzenle", edit, modifier = Modifier.weight(1f))
                }
                FigmaSecondaryButton("Kapat", onClose, modifier = Modifier.weight(1f))
            } else {
                FigmaPrimaryButton("Yeniden aç", onReopen, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
