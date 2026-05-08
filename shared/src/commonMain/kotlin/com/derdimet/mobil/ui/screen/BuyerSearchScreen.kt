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
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

private data class BuyerSearchFilters(
    val sort: String = "newest",
    val meatType: String = "",
    val quantityMin: String = "",
    val quantityMax: String = "",
)

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
    var filters by remember { mutableStateOf(BuyerSearchFilters()) }
    var filterOpen by remember { mutableStateOf(false) }

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
        val meatType = filters.meatType.trim().lowercase()
        val qMin = parseDoubleOrNull(filters.quantityMin)
        val qMax = parseDoubleOrNull(filters.quantityMax)

        fun matches(item: MeatSaleRequestDto): Boolean {
            if (q.isNotBlank()) {
                val inTitle = item.title.lowercase().contains(q)
                val inMeat = item.meatType.lowercase().contains(q)
                val inSh = (item.slaughterhouseName ?: "").lowercase().contains(q)
                val inCo = (item.slaughterhouseCompanyName ?: "").lowercase().contains(q)
                if (!inTitle && !inMeat && !inSh && !inCo) return false
            }
            if (meatType.isNotBlank() && !item.meatType.lowercase().contains(meatType)) return false
            val qty = item.quantity
            if (qMin != null && (qty == null || qty < qMin)) return false
            if (qMax != null && (qty == null || qty > qMax)) return false
            return true
        }

        val base = listings.filter(::matches)
        when (filters.sort) {
            "qtyasc" -> base.sortedBy { it.quantity ?: Double.MAX_VALUE }
            "qtydesc" -> base.sortedByDescending { it.quantity ?: Double.MIN_VALUE }
            else -> base
        }
    }

    val activeFilterCount = remember(filters) {
        listOf(
            filters.sort != "newest",
            filters.meatType.isNotBlank(),
            filters.quantityMin.isNotBlank(),
            filters.quantityMax.isNotBlank(),
        ).count { it }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(text = "🥩 Et İlanları", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Kesimhanelerin açık ilanları", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Text(
                        text = "${filteredListings.size} ilan",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Başlık, et türü, kesimhane...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedButton(onClick = { filterOpen = true }) {
                        Text(if (activeFilterCount > 0) "Filtre ($activeFilterCount)" else "Filtre")
                    }
                }
                if (activeFilterCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        @Composable
                        fun Chip(text: String, onClear: () -> Unit) {
                            TextButton(
                                onClick = onClear,
                                modifier = Modifier.background(Color(0xFF1B3A6B).copy(alpha = 0.10f), RoundedCornerShape(999.dp)),
                            ) {
                                Text("$text  ✕", color = Color(0xFF1B3A6B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        if (filters.sort != "newest") Chip(
                            text = when (filters.sort) {
                                "qtyasc" -> "Azdan çoğa"
                                "qtydesc" -> "Çoktan aza"
                                else -> "Sıralama"
                            }
                        ) { filters = filters.copy(sort = "newest") }
                        if (filters.meatType.isNotBlank()) Chip("Tür: ${filters.meatType}") { filters = filters.copy(meatType = "") }
                        if (filters.quantityMin.isNotBlank()) Chip("Kg≥${filters.quantityMin}") { filters = filters.copy(quantityMin = "") }
                        if (filters.quantityMax.isNotBlank()) Chip("Kg≤${filters.quantityMax}") { filters = filters.copy(quantityMax = "") }
                    }
                }
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            filteredListings.isEmpty() -> Text("Uygun ilan bulunamadı.", color = Color(0xFF64748B))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filteredListings) { item ->
                    val sid = item.slaughterhouseId
                    val isFav = sid != null && favoriteSlaughterhouseIds.contains(sid)
                    FigmaCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { detailListingId = item.id },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "Et türü: ${item.meatType} • Miktar: ${item.quantity ?: "-"} kg",
                                        color = Color(0xFF64748B),
                                    )
                                    Text(
                                        text = "Kesimhane: ${item.slaughterhouseCompanyName ?: item.slaughterhouseName ?: (item.slaughterhouseId ?: "-")}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                    )
                                }
                                IconButton(
                                    enabled = sid != null && favSubmittingId != sid,
                                    onClick = { if (sid != null) favSubmittingId = sid },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = if (isFav) "Favoriden çıkar" else "Favorile",
                                        tint = if (isFav) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                FigmaSecondaryButton(
                                    text = "Detay",
                                    onClick = { detailListingId = item.id },
                                    modifier = Modifier.weight(1f),
                                )
                                FigmaPrimaryButton(
                                    text = "Teklif ver",
                                    onClick = { offerForListing = item },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
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
                    FigmaSecondaryButton("Azdan", onClick = { filters = filters.copy(sort = "qtyasc") }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Çoktan", onClick = { filters = filters.copy(sort = "qtydesc") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = filters.meatType,
                    onValueChange = { filters = filters.copy(meatType = it) },
                    label = { Text("Et türü (örn: dana)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filters.quantityMin,
                        onValueChange = { filters = filters.copy(quantityMin = it) },
                        label = { Text("Kg min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = filters.quantityMax,
                        onValueChange = { filters = filters.copy(quantityMax = it) },
                        label = { Text("Kg max") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Sıfırla",
                        onClick = { filters = BuyerSearchFilters() },
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
