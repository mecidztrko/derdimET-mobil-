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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.CreateMeatOfferPayload
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimListingCard
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.DefaultSearchFilters
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.FilterChipButton
import com.derdimet.mobil.ui.components.MarketplaceSearchBar
import com.derdimet.mobil.ui.components.MeatTypeChips
import com.derdimet.mobil.ui.components.SearchFilterSheet
import com.derdimet.mobil.ui.components.SearchFilters
import com.derdimet.mobil.ui.components.SortOptionSheet
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.filterMeatListings
import com.derdimet.mobil.util.toggleFavoriteIdSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<MeatSaleRequestDto>>(emptyList()) }
    var favoriteListingIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var favSubmittingId by remember { mutableStateOf<Long?>(null) }
    var favToggleNonce by remember { mutableIntStateOf(0) }
    var favoriteError by remember { mutableStateOf<String?>(null) }

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(DefaultSearchFilters) }
    var filterOpen by remember { mutableStateOf(false) }
    var sortOpen by remember { mutableStateOf(false) }

    var detailListing by remember { mutableStateOf<MeatSaleRequestDto?>(null) }
    var offerForListing by remember { mutableStateOf<MeatSaleRequestDto?>(null) }
    var chatTargetUserId by remember { mutableStateOf<Long?>(null) }
    var chatLaunchNonce by remember { mutableIntStateOf(0) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.fetchOpenMeatSaleRequests()
        if (res.success) {
            val data = res.data ?: emptyList()
            listings = data
            favoriteListingIds = data.filter { it.isFavoritedByMe == true }.map { it.id }.toSet()
        } else {
            error = res.message ?: "Liste alınamadı"
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    LaunchedEffect(favSubmittingId, favToggleNonce) {
        val listingId = favSubmittingId ?: return@LaunchedEffect
        favoriteError = null
        try {
            val res = marketService.toggleMeatListingFavorite(listingId)
            if (res.success) {
                val nowFav = res.data?.isFavoritedByMe == true
                favoriteListingIds = toggleFavoriteIdSet(favoriteListingIds, listingId, nowFav)
                listings = listings.map { if (it.id == listingId) it.copy(isFavoritedByMe = nowFav) else it }
                detailListing = detailListing?.takeIf { it.id == listingId }?.copy(isFavoritedByMe = nowFav) ?: detailListing
            } else {
                favoriteError = res.message ?: "Favori işlemi başarısız"
            }
        } finally {
            if (favSubmittingId == listingId) favSubmittingId = null
        }
    }

    LaunchedEffect(chatTargetUserId, chatLaunchNonce) {
        val otherId = chatTargetUserId ?: return@LaunchedEffect
        try {
            val res = marketService.getOrCreateConversation(otherId)
            if (res.success && res.data != null) {
                selectedConversation = res.data
            } else {
                error = res.message ?: "Sohbet başlatılamadı"
            }
        } finally {
            if (chatTargetUserId == otherId) chatTargetUserId = null
        }
    }

    val offerListing = offerForListing
    if (offerListing != null) {
        OfferCreateScreen(
            title = offerListing.title,
            subtitle = "Kesimhane: ${offerListing.slaughterhouseCompanyName ?: offerListing.slaughterhouseName ?: "-"}",
            contextLine = "Et türü: ${offerListing.meatType} • Toplam: ${offerListing.quantity ?: "-"} kg",
            showQuantityAsInt = false,
            quantityLabel = "Miktar (kg)",
            referencePricePerKg = offerListing.pricePerKg,
            onBack = { offerForListing = null },
            onSuccess = {
                offerForListing = null
                detailListing = null
            },
            submit = { price, qty, note ->
                val res = marketService.createBuyerMeatOffer(
                    saleRequestId = offerListing.id,
                    payload = CreateMeatOfferPayload(
                        pricePerKg = price,
                        quantity = qty,
                        note = note,
                    ),
                )
                Pair(res.success, res.message)
            },
        )
        return
    }

    val detailItem = detailListing
    if (detailItem != null) {
        MeatSaleRequestDetailScreen(
            saleRequestId = detailItem.id,
            initialListing = detailItem,
            isFavorited = favoriteListingIds.contains(detailItem.id),
            favoriteError = favoriteError,
            onFavoriteToggle = {
                favSubmittingId = detailItem.id
                favToggleNonce++
            },
            marketService = marketService,
            onBack = { detailListing = null },
            onMakeOffer = { item -> offerForListing = item },
            onMessage = { sid ->
                detailListing = null
                chatTargetUserId = sid
                chatLaunchNonce++
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
                    detailListing = null
                    chatTargetUserId = id
                    chatLaunchNonce++
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

    val filteredListings = remember(listings, query, filters) {
        filterMeatListings(listings, query, filters)
    }

    val activeFilterCount = remember(filters) {
        listOf(
            filters.type.isNotBlank(),
            filters.city.isNotBlank(),
            filters.priceMin.isNotBlank() || filters.priceMax.isNotBlank(),
            filters.weightMin.isNotBlank() || filters.weightMax.isNotBlank(),
            filters.verifiedOnly,
        ).count { it }
    }

    Column(modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        favoriteError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        DerdimTopBar(showLogo = true, subtitle = "Et İlanları")
        DerdimListScreenBody(
            header = {
                MarketplaceSearchBar(value = query, onValueChange = { query = it }, placeholder = "Et türü, şehir veya satıcı ara...", onFilterClick = { filterOpen = true }, activeFilterCount = activeFilterCount)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MeatTypeChips.forEach { chip ->
                        FilterChipButton(
                            label = chip,
                            selected = filters.type.equals(chip, true) || (chip == "Tümü" && filters.type.isBlank()),
                            onClick = { filters = filters.copy(type = if (chip == "Tümü") "" else chip) },
                        )
                    }
                    FilterChipButton("Sırala", false, onClick = { sortOpen = true })
                }
                Text("${filteredListings.size} ilan bulundu", fontSize = 12.sp, color = DerdimColors.MutedForeground)
            },
            content = {
                when {
                    isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    filteredListings.isEmpty() -> Text("Uygun ilan bulunamadı.", color = DerdimColors.MutedForeground)
                    else -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredListings.size) { index ->
                            val item = filteredListings[index]
                            DerdimListingCard(
                                item = item,
                                index = index,
                                isFavorited = favoriteListingIds.contains(item.id),
                                onFavoriteClick = {
                                    favSubmittingId = item.id
                                    favToggleNonce++
                                },
                                onClick = {
                                    detailListing = item.copy(
                                        isFavoritedByMe = favoriteListingIds.contains(item.id),
                                    )
                                },
                                onOfferClick = { offerForListing = item },
                            )
                        }
                    }
                }
            },
        )
    }

    if (filterOpen) {
        ModalBottomSheet(onDismissRequest = { filterOpen = false }) {
            SearchFilterSheet(
                filters = filters,
                countForFilters = { preview -> filterMeatListings(listings, query, preview).size },
                onApply = { filters = it },
                onDismiss = { filterOpen = false },
            )
        }
    }
    if (sortOpen) {
        ModalBottomSheet(onDismissRequest = { sortOpen = false }) {
            SortOptionSheet(current = filters.sort, onSelect = { filters = filters.copy(sort = it) }, onDismiss = { sortOpen = false })
        }
    }
}
