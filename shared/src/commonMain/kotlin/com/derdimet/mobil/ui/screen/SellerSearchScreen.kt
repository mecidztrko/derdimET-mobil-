package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.CreateAnimalOfferPayload
import com.derdimet.mobil.model.RequestStatus
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimAnimalPurchaseCard
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FilterChipButton
import com.derdimet.mobil.util.formatNumber
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.MarketplaceSearchBar
import com.derdimet.mobil.ui.theme.DerdimColors

private data class SellerReqFilters(
    val sort: String = "newest",
    val category: AnimalCategory? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchTab by remember { mutableStateOf("my_listings") }
    var requests by remember { mutableStateOf<List<AnimalPurchaseRequestDto>>(emptyList()) }
    var myListings by remember { mutableStateOf<List<SellerAnimalListingDto>>(emptyList()) }
    var editingListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }
    var listingActionMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var favoriteRequestIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var favSubmittingId by remember { mutableStateOf<Int?>(null) }
    var favToggleNonce by remember { mutableIntStateOf(0) }
    var favoriteError by remember { mutableStateOf<String?>(null) }

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(SellerReqFilters()) }
    var filterOpen by remember { mutableStateOf(false) }
    var refreshTick by remember { mutableStateOf(0) }

    var detailRequest by remember { mutableStateOf<AnimalPurchaseRequestDto?>(null) }
    var offerForRequest by remember { mutableStateOf<AnimalPurchaseRequestDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(query, filters, refreshTick, searchTab) {
        isLoading = true
        error = null
        val myRes = marketService.fetchMySellerAnimalListings(q = query.takeIf { it.isNotBlank() })
        if (myRes.success) {
            myListings = myRes.data ?: emptyList()
        } else if (searchTab == "my_listings") {
            error = myRes.message ?: "İlanlarınız alınamadı"
        }

        if (searchTab == "requests") {
            val res = marketService.fetchOpenAnimalPurchaseRequestsFiltered(
                category = filters.category?.name,
                q = query.takeIf { it.isNotBlank() },
                sort = filters.sort,
            )
            if (res.success) {
                val data = res.data ?: emptyList()
                requests = data
                favoriteRequestIds = data.filter { it.isFavoritedByMe == true }.map { it.id }.toSet()
            } else {
                error = res.message ?: "Talepler alınamadı"
            }
        }
        isLoading = false
    }

    val listingToEdit = editingListing
    if (listingToEdit != null) {
        SellerEditListingScreen(
            listing = listingToEdit,
            marketService = marketService,
            onBack = { editingListing = null },
            onSaved = {
                editingListing = null
                listingActionMessage = "İlan güncellendi."
                refreshTick++
            },
        )
        return
    }

    LaunchedEffect(favSubmittingId, favToggleNonce) {
        val requestId = favSubmittingId ?: return@LaunchedEffect
        favoriteError = null
        try {
            val res = marketService.toggleAnimalPurchaseRequestFavorite(requestId.toLong())
            if (res.success) {
                val nowFav = res.data?.isFavoritedByMe == true
                favoriteRequestIds = if (nowFav) favoriteRequestIds + requestId else favoriteRequestIds - requestId
                requests = requests.map { if (it.id == requestId) it.copy(isFavoritedByMe = nowFav) else it }
                detailRequest = detailRequest?.takeIf { it.id == requestId }?.copy(isFavoritedByMe = nowFav) ?: detailRequest
            } else {
                favoriteError = res.message ?: "Favori işlemi başarısız"
            }
        } finally {
            if (favSubmittingId == requestId) favSubmittingId = null
        }
    }

    LaunchedEffect(startChatWithUserId) {
        val otherId = startChatWithUserId ?: return@LaunchedEffect
        startChatWithUserId = null
        val res = marketService.getOrCreateConversation(otherId)
        if (res.success && res.data != null) {
            selectedConversation = res.data
        } else {
            error = res.message ?: "Sohbet başlatılamadı"
        }
    }

    val offerReq = offerForRequest
    if (offerReq != null) {
        OfferCreateScreen(
            title = offerReq.title,
            subtitle = "Kesimhane: ${offerReq.slaughterhouseCompanyName ?: offerReq.slaughterhouseName ?: "-"}",
            contextLine = "Kategori: ${offerReq.animalCategory ?: "-"} • İhtiyaç: ${offerReq.quantity ?: "-"}",
            showQuantityAsInt = true,
            quantityLabel = "Adet",
            onBack = { offerForRequest = null },
            onSuccess = {
                offerForRequest = null
                detailRequest = null
                refreshTick++
            },
            submit = { price, qty, note ->
                val res = marketService.createAnimalOffer(
                    requestId = offerReq.id,
                    payload = CreateAnimalOfferPayload(
                        pricePerKg = price,
                        animalCount = qty.toInt(),
                        note = note,
                    ),
                )
                Pair(res.success, res.message)
            },
        )
        return
    }

    val detailItem = detailRequest
    if (detailItem != null) {
        AnimalPurchaseRequestDetailScreen(
            requestId = detailItem.id.toLong(),
            initialRequest = detailItem,
            isFavorited = favoriteRequestIds.contains(detailItem.id),
            favoriteError = favoriteError,
            onFavoriteToggle = {
                favSubmittingId = detailItem.id
                favToggleNonce++
            },
            marketService = marketService,
            onBack = { detailRequest = null },
            onMakeOffer = { item -> offerForRequest = item },
            onMessage = { sid ->
                detailRequest = null
                startChatWithUserId = sid
            },
            onOpenSlaughterhouseProfile = { sid -> openProfileUserId = sid },
        )
        val openId = openProfileUserId
        if (openId != null) {
            PublicProfileScreen(
                userId = openId,
                marketService = marketService,
                onBack = { openProfileUserId = null },
                onMessage = { id ->
                    openProfileUserId = null
                    detailRequest = null
                    startChatWithUserId = id
                },
            )
        }
        return
    }

    val convo = selectedConversation
    if (convo != null) {
        ChatScreen(
            marketService = marketService,
            conversationId = convo.conversationId,
            title = convo.otherUserName ?: (convo.otherUserEmail ?: "Sohbet"),
            onBack = { selectedConversation = null },
        )
        return
    }

    val activeFilterCount = remember(filters) {
        listOf(filters.category != null, filters.sort != "newest").count { it }
    }

    Column(modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        favoriteError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        DerdimTopBar(showLogo = true, subtitle = if (searchTab == "my_listings") "İlanlarım" else "Kesimhane Talepleri")
        DerdimListScreenBody(
            header = {
                DerdimFilterTabs(
                    tabs = listOf(
                        Triple("my_listings", "İlanlarım", myListings.size),
                        Triple("requests", "Kesimhane Talepleri", requests.size),
                    ),
                    selectedKey = searchTab,
                    onSelect = { searchTab = it },
                )
                listingActionMessage?.let {
                    Text(it, fontSize = 12.sp, color = Color(0xFF166534))
                }
                MarketplaceSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = if (searchTab == "my_listings") "İlanlarımda ara..." else "Talep başlığı veya kesimhane ara...",
                    onFilterClick = { filterOpen = true },
                    activeFilterCount = if (searchTab == "requests") activeFilterCount else 0,
                )
                if (searchTab == "requests") {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChipButton("Tümü", filters.category == null, onClick = { filters = filters.copy(category = null) })
                        FilterChipButton("Küçükbaş", filters.category == AnimalCategory.KUCUKBAS, onClick = { filters = filters.copy(category = AnimalCategory.KUCUKBAS) })
                        FilterChipButton("Büyükbaş", filters.category == AnimalCategory.BUYUKBAS, onClick = { filters = filters.copy(category = AnimalCategory.BUYUKBAS) })
                    }
                    Text("${requests.size} talep bulundu", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                } else {
                    Text("${myListings.size} ilan", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                }
            },
            content = {
                when {
                    isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    searchTab == "my_listings" && myListings.isEmpty() -> Text(
                        "Henüz ilanınız yok. + sekmesinden yeni ilan oluşturabilirsiniz.",
                        color = DerdimColors.MutedForeground,
                    )
                    searchTab == "my_listings" -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(myListings, key = { it.id }) { listing ->
                            ListingManageCard(
                                title = "${listing.type} · ${listing.quantity} adet",
                                subtitle = listing.location ?: listing.sellerCity ?: "—",
                                price = listing.price?.let { "${formatNumber(it)} ₺" },
                                status = listing.status,
                                onClose = {
                                    scope.launch {
                                        marketService.closeSellerAnimalListing(listing.id)
                                        listingActionMessage = "İlan kapatıldı."
                                        refreshTick++
                                    }
                                },
                                onReopen = {
                                    scope.launch {
                                        marketService.reopenSellerAnimalListing(listing.id)
                                        listingActionMessage = "İlan yeniden açıldı."
                                        refreshTick++
                                    }
                                },
                                onEdit = if (listing.status == RequestStatus.OPEN) {
                                    { editingListing = listing }
                                } else null,
                            )
                        }
                    }
                    searchTab == "requests" && requests.isEmpty() -> Text("Uygun talep bulunamadı.", color = DerdimColors.MutedForeground)
                    else -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(requests.size) { index ->
                            val r = requests[index]
                            DerdimAnimalPurchaseCard(
                                item = r,
                                index = index,
                                isFavorited = favoriteRequestIds.contains(r.id),
                                onFavoriteClick = {
                                    favSubmittingId = r.id
                                    favToggleNonce++
                                },
                                onClick = {
                                    detailRequest = r.copy(
                                        isFavoritedByMe = favoriteRequestIds.contains(r.id),
                                    )
                                },
                                onOfferClick = { offerForRequest = r },
                            )
                        }
                    }
                }
            },
        )
    }

    if (filterOpen && searchTab == "requests") {
        ModalBottomSheet(onDismissRequest = { filterOpen = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "Filtrele", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Sıralama", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FigmaSecondaryButton("En yeni", onClick = { filters = filters.copy(sort = "newest") }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Eski", onClick = { filters = filters.copy(sort = "oldest") }, modifier = Modifier.weight(1f))
                }
                Text(text = "Kategori", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FigmaSecondaryButton("Tümü", onClick = { filters = filters.copy(category = null) }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Küçükbaş", onClick = { filters = filters.copy(category = AnimalCategory.KUCUKBAS) }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Büyükbaş", onClick = { filters = filters.copy(category = AnimalCategory.BUYUKBAS) }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Sıfırla",
                        onClick = { filters = SellerReqFilters() },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaPrimaryButton(
                        text = "Uygula",
                        onClick = { filterOpen = false },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
