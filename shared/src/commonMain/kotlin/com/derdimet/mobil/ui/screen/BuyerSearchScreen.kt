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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Tune

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<MeatSaleRequestDto>>(emptyList()) }
    var favoriteSlaughterhouseIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var favSubmittingId by remember { mutableStateOf<Long?>(null) }

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(DefaultSearchFilters) }
    var filterOpen by remember { mutableStateOf(false) }
    var sortOpen by remember { mutableStateOf(false) }

    var detailListingId by remember { mutableStateOf<Long?>(null) }
    var offerForListing by remember { mutableStateOf<MeatSaleRequestDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    fun parseDoubleOrNull(s: String): Double? = s.trim().takeIf { it.isNotEmpty() }?.replace(',', '.')?.toDoubleOrNull()

    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.fetchOpenMeatSaleRequests()
        if (res.success) {
            listings = res.data ?: emptyList()
        } else {
            error = res.message ?: "Liste alınamadı"
        }
        isLoading = false
    }

    suspend fun refreshFavorites() {
        val fav = marketService.fetchBuyerFavoriteSlaughterhouses()
        if (fav.success) {
            favoriteSlaughterhouseIds = (fav.data ?: emptyList()).map { it.slaughterhouseId }.toSet()
        }
    }

    LaunchedEffect(Unit) {
        refresh()
        refreshFavorites()
    }

    val detailId = detailListingId
    if (detailId != null) {
        MeatSaleRequestDetailScreen(
            saleRequestId = detailId,
            marketService = marketService,
            onBack = { detailListingId = null },
            onMakeOffer = { item -> offerForListing = item },
            onMessage = { sid ->
                detailListingId = null
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
                    detailListingId = null
                    startChatWithUserId = id
                },
            )
        }
        return
    }

    val offerListing = offerForListing
    if (offerListing != null) {
        OfferCreateScreen(
            title = offerListing.title,
            subtitle = "Kesimhane: ${offerListing.slaughterhouseCompanyName ?: offerListing.slaughterhouseName ?: "-"}",
            contextLine = "Et türü: ${offerListing.meatType} • Toplam: ${offerListing.quantity ?: "-"} kg",
            showQuantityAsInt = false,
            quantityLabel = "Miktar (kg)",
            onBack = { offerForListing = null },
            onSuccess = {
                offerForListing = null
                detailListingId = null
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
        val q = query.trim().lowercase()
        val meatType = filters.type.trim().lowercase()
        val city = filters.city.trim().lowercase()
        val wMin = parseDoubleOrNull(filters.weightMin)
        val wMax = parseDoubleOrNull(filters.weightMax)
        val pMin = parseDoubleOrNull(filters.priceMin)
        val pMax = parseDoubleOrNull(filters.priceMax)

        fun matches(item: MeatSaleRequestDto): Boolean {
            if (q.isNotBlank()) {
                val hay = listOf(item.title, item.meatType, item.slaughterhouseName, item.slaughterhouseCompanyName, item.location, item.slaughterhouseCity)
                    .joinToString(" ").lowercase()
                if (!hay.contains(q)) return false
            }
            if (meatType.isNotBlank() && !item.meatType.lowercase().contains(meatType)) return false
            if (city.isNotBlank()) {
                val loc = "${item.location ?: ""} ${item.slaughterhouseCity ?: ""}".lowercase()
                if (!loc.contains(city)) return false
            }
            val qty = item.quantity
            if (wMin != null && (qty == null || qty < wMin)) return false
            if (wMax != null && (qty == null || qty > wMax)) return false
            val price = item.pricePerKg
            if (pMin != null && (price == null || price < pMin)) return false
            if (pMax != null && (price == null || price > pMax)) return false
            return true
        }

        val base = listings.filter(::matches)
        when (filters.sort) {
            "lowest" -> base.sortedBy { it.pricePerKg ?: Double.MAX_VALUE }
            "highest" -> base.sortedByDescending { it.pricePerKg ?: Double.MIN_VALUE }
            "qtyasc" -> base.sortedBy { it.quantity ?: Double.MAX_VALUE }
            "qtydesc" -> base.sortedByDescending { it.quantity ?: Double.MIN_VALUE }
            else -> base
        }
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
        DerdimTopBar(showLogo = true, subtitle = "Et İlanları", action = {
            IconButton(onClick = { filterOpen = true }) {
                Box {
                    Icon(Icons.Default.Tune, contentDescription = "Filtre", tint = DerdimColors.Primary)
                    if (activeFilterCount > 0) {
                        Box(Modifier.align(Alignment.TopEnd).size(8.dp).background(DerdimColors.Destructive, CircleShape))
                    }
                }
            }
        })
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
            when {
                isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                filteredListings.isEmpty() -> Text("Uygun ilan bulunamadı.", color = DerdimColors.MutedForeground)
                else -> LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredListings.size) { index ->
                        val item = filteredListings[index]
                        val sid = item.slaughterhouseId
                        DerdimListingCard(
                            item = item,
                            index = index,
                            isFavorited = sid != null && favoriteSlaughterhouseIds.contains(sid),
                            onFavoriteClick = { if (sid != null) favSubmittingId = sid },
                            onClick = { detailListingId = item.id },
                            onOfferClick = { offerForListing = item },
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(favSubmittingId) {
        val sid = favSubmittingId ?: return@LaunchedEffect
        favSubmittingId = null
        val res = marketService.toggleFavorite(sid)
        if (res.success) {
            refreshFavorites()
        } else {
            error = res.message ?: "Favori işlemi başarısız"
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

    if (filterOpen) {
        ModalBottomSheet(onDismissRequest = { filterOpen = false }) {
            SearchFilterSheet(filters = filters, onApply = { filters = it }, onDismiss = { filterOpen = false })
        }
    }
    if (sortOpen) {
        ModalBottomSheet(onDismissRequest = { sortOpen = false }) {
            SortOptionSheet(current = filters.sort, onSelect = { filters = filters.copy(sort = it) }, onDismiss = { sortOpen = false })
        }
    }
}
