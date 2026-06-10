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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.CreateSlaughterhouseListingOfferPayload
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.util.toggleFavoriteIdSet
import com.derdimet.mobil.ui.components.DerdimAnimalListingCard
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FilterChipButton
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.MarketplaceSearchBar
import com.derdimet.mobil.ui.theme.DerdimColors

private data class ShFilters(
    val sort: String = "newest",
    val category: AnimalCategory? = null,
    val type: String = "",
    val ageMin: String = "",
    val ageMax: String = "",
    val quantityMin: String = "",
    val quantityMax: String = "",
    val priceMin: String = "",
    val priceMax: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlaughterhouseSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<SellerAnimalListingDto>>(emptyList()) }
    var favoriteSellerIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var favSubmittingSellerId by remember { mutableStateOf<Long?>(null) }
    var favToggleNonce by remember { mutableIntStateOf(0) }
    var favoriteError by remember { mutableStateOf<String?>(null) }

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(ShFilters()) }
    var filterOpen by remember { mutableStateOf(false) }

    var detailListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }
    var offerForListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    fun parseIntOrNull(s: String): Int? = s.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    fun parseDoubleOrNull(s: String): Double? = s.trim().takeIf { it.isNotEmpty() }?.replace(',', '.')?.toDoubleOrNull()

    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.searchSlaughterhouseAnimalListingsFiltered(
            category = filters.category?.name,
            type = filters.type.takeIf { it.isNotBlank() } ?: query.takeIf { it.isNotBlank() },
            ageMin = parseIntOrNull(filters.ageMin),
            ageMax = parseIntOrNull(filters.ageMax),
            quantityMin = parseIntOrNull(filters.quantityMin),
            quantityMax = parseIntOrNull(filters.quantityMax),
            priceMin = parseDoubleOrNull(filters.priceMin),
            priceMax = parseDoubleOrNull(filters.priceMax),
            sort = filters.sort,
        )
        if (res.success) {
            val all = res.data ?: emptyList()
            listings =
                if (query.isBlank()) all
                else all.filter {
                    val q = query.trim().lowercase()
                    it.type.lowercase().contains(q) || (it.sellerName ?: "").lowercase().contains(q)
                }
        } else {
            error = res.message ?: "İlanlar alınamadı"
        }
        isLoading = false
    }

    suspend fun refreshFavorites() {
        val fav = marketService.fetchSlaughterhouseFavoriteSellers()
        if (fav.success) {
            favoriteSellerIds = (fav.data ?: emptyList()).mapNotNull { it.sellerId }.toSet()
        }
    }

    LaunchedEffect(filters, query) { refresh() }
    LaunchedEffect(Unit) { refreshFavorites() }

    LaunchedEffect(favSubmittingSellerId, favToggleNonce) {
        val sid = favSubmittingSellerId ?: return@LaunchedEffect
        favoriteError = null
        try {
            val res = marketService.toggleFavorite(sid)
            if (res.success) {
                val nowFav = res.data?.isFavoritedByMe == true
                favoriteSellerIds = toggleFavoriteIdSet(favoriteSellerIds, sid, nowFav)
                listings = listings.map { if (it.sellerId == sid) it.copy(isFavoritedByMe = nowFav) else it }
                detailListing = detailListing?.takeIf { it.sellerId == sid }?.copy(isFavoritedByMe = nowFav) ?: detailListing
            } else {
                favoriteError = res.message ?: "Favori işlemi başarısız"
            }
        } finally {
            if (favSubmittingSellerId == sid) favSubmittingSellerId = null
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

    // Offer create overlay (detayın üstünde açılır)
    val offerListing = offerForListing
    if (offerListing != null) {
        OfferCreateScreen(
            title = offerListing.type,
            subtitle = "Satıcı: ${offerListing.sellerName ?: "-"}",
            contextLine = "Adet: ${offerListing.quantity} • Fiyat: ${offerListing.price ?: "-"}",
            showQuantityAsInt = true,
            quantityLabel = "Adet",
            onBack = { offerForListing = null },
            onSuccess = {
                offerForListing = null
                detailListing = null
            },
            submit = { price, qty, note ->
                val res = marketService.createSlaughterhouseListingOffer(
                    listingId = offerListing.id,
                    payload = CreateSlaughterhouseListingOfferPayload(
                        pricePerKg = price,
                        quantity = qty.toInt(),
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
        val favId = detailItem.sellerId
        SellerAnimalListingDetailScreen(
            listingId = detailItem.id,
            initialListing = detailItem,
            isFavorited = favId != null && favoriteSellerIds.contains(favId),
            favoriteError = favoriteError,
            onFavoriteToggle = {
                if (favId != null) {
                    favSubmittingSellerId = favId
                    favToggleNonce++
                }
            },
            marketService = marketService,
            onBack = { detailListing = null },
            onMakeOffer = { l -> offerForListing = l },
            onMessage = { sid ->
                detailListing = null
                startChatWithUserId = sid
            },
            onOpenSellerProfile = { sid -> openProfileUserId = sid },
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
                    startChatWithUserId = id
                },
            )
        }
        return
    }

    // Chat overlay
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
        listOf(
            filters.sort != "newest",
            filters.category != null,
            filters.type.isNotBlank(),
            filters.ageMin.isNotBlank(),
            filters.ageMax.isNotBlank(),
            filters.quantityMin.isNotBlank(),
            filters.quantityMax.isNotBlank(),
            filters.priceMin.isNotBlank(),
            filters.priceMax.isNotBlank(),
        ).count { it }
    }

    Column(modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        favoriteError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        DerdimTopBar(showLogo = true, subtitle = "Hayvan İlanları")
        DerdimListScreenBody(
            header = {
                MarketplaceSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Hayvan türü, ırk veya satıcı ara...",
                    onFilterClick = { filterOpen = true },
                    activeFilterCount = activeFilterCount,
                )
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipButton("Tümü", filters.category == null, onClick = { filters = filters.copy(category = null) })
                    FilterChipButton("Küçükbaş", filters.category == AnimalCategory.KUCUKBAS, onClick = { filters = filters.copy(category = AnimalCategory.KUCUKBAS) })
                    FilterChipButton("Büyükbaş", filters.category == AnimalCategory.BUYUKBAS, onClick = { filters = filters.copy(category = AnimalCategory.BUYUKBAS) })
                }
                Text("${listings.size} ilan bulundu", fontSize = 12.sp, color = DerdimColors.MutedForeground)
            },
            content = {
                when {
                    isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    listings.isEmpty() -> Text("Uygun ilan bulunamadı.", color = DerdimColors.MutedForeground)
                    else -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listings.size) { index ->
                            val l = listings[index]
                            val sid = l.sellerId
                            DerdimAnimalListingCard(
                                item = l,
                                index = index,
                                isFavorited = sid != null && favoriteSellerIds.contains(sid),
                                onFavoriteClick = {
                                    if (sid != null) {
                                        favSubmittingSellerId = sid
                                        favToggleNonce++
                                    }
                                },
                                onClick = {
                                    detailListing = l.copy(
                                        isFavoritedByMe = sid != null && favoriteSellerIds.contains(sid),
                                    )
                                },
                                onOfferClick = { offerForListing = l },
                            )
                        }
                    }
                }
            },
        )
    }

    if (filterOpen) {
        ModalBottomSheet(
            onDismissRequest = { filterOpen = false },
        ) {
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
                    FigmaSecondaryButton("Ucuzdan", onClick = { filters = filters.copy(sort = "priceasc") }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Pahalıdan", onClick = { filters = filters.copy(sort = "pricedesc") }, modifier = Modifier.weight(1f))
                }
                Text(text = "Kategori", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FigmaSecondaryButton("Tümü", onClick = { filters = filters.copy(category = null) }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Küçükbaş", onClick = { filters = filters.copy(category = AnimalCategory.KUCUKBAS) }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Büyükbaş", onClick = { filters = filters.copy(category = AnimalCategory.BUYUKBAS) }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = filters.type,
                    onValueChange = { filters = filters.copy(type = it) },
                    label = { Text("Tür (ör: Merinos)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filters.ageMin,
                        onValueChange = { filters = filters.copy(ageMin = it) },
                        label = { Text("Yaş min (ay)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = filters.ageMax,
                        onValueChange = { filters = filters.copy(ageMax = it) },
                        label = { Text("Yaş max (ay)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filters.quantityMin,
                        onValueChange = { filters = filters.copy(quantityMin = it) },
                        label = { Text("Adet min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = filters.quantityMax,
                        onValueChange = { filters = filters.copy(quantityMax = it) },
                        label = { Text("Adet max") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filters.priceMin,
                        onValueChange = { filters = filters.copy(priceMin = it) },
                        label = { Text("Fiyat min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = filters.priceMax,
                        onValueChange = { filters = filters.copy(priceMax = it) },
                        label = { Text("Fiyat max") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton("Sıfırla", onClick = { filters = ShFilters() }, modifier = Modifier.weight(1f))
                    FigmaPrimaryButton("Uygula", onClick = { filterOpen = false }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
