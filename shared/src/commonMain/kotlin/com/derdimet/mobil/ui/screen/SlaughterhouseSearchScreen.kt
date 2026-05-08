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
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.CreateSlaughterhouseListingOfferPayload
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

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

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(ShFilters()) }
    var filterOpen by remember { mutableStateOf(false) }

    var detailListingId by remember { mutableStateOf<Long?>(null) }
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

    // Detail screen overlay
    val detailId = detailListingId
    if (detailId != null) {
        SellerAnimalListingDetailScreen(
            listingId = detailId,
            marketService = marketService,
            onBack = { detailListingId = null },
            onMakeOffer = { l -> offerForListing = l },
            onMessage = { sid ->
                detailListingId = null
                startChatWithUserId = sid
            },
            onOpenSellerProfile = { sid -> openProfileUserId = sid },
        )
        // Profile inside detail
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

    // Offer create overlay
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
                detailListingId = null
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
                        Text(text = "🐄 Hayvan İlanları", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Satıcıların aktif ilanları", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Text(
                        text = "${listings.size} ilan",
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
                        label = { Text("Hayvan türü, satıcı...") },
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
                                "priceasc" -> "Ucuzdan"
                                "pricedesc" -> "Pahalıdan"
                                else -> "Sıralama"
                            }
                        ) { filters = filters.copy(sort = "newest") }
                        if (filters.category != null) Chip(filters.category!!.name) { filters = filters.copy(category = null) }
                        if (filters.type.isNotBlank()) Chip("Tür: ${filters.type}") { filters = filters.copy(type = "") }
                        if (filters.ageMin.isNotBlank()) Chip("Yaş≥${filters.ageMin}") { filters = filters.copy(ageMin = "") }
                        if (filters.ageMax.isNotBlank()) Chip("Yaş≤${filters.ageMax}") { filters = filters.copy(ageMax = "") }
                        if (filters.quantityMin.isNotBlank()) Chip("Adet≥${filters.quantityMin}") { filters = filters.copy(quantityMin = "") }
                        if (filters.quantityMax.isNotBlank()) Chip("Adet≤${filters.quantityMax}") { filters = filters.copy(quantityMax = "") }
                        if (filters.priceMin.isNotBlank()) Chip("₺≥${filters.priceMin}") { filters = filters.copy(priceMin = "") }
                        if (filters.priceMax.isNotBlank()) Chip("₺≤${filters.priceMax}") { filters = filters.copy(priceMax = "") }
                    }
                }
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(listings) { l ->
                    val sid = l.sellerId
                    val isFav = sid != null && favoriteSellerIds.contains(sid)
                    FigmaCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { detailListingId = l.id },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "${l.type} · ${l.category}", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "Yaş: ${l.ageMonths ?: "-"} ay • Adet: ${l.quantity}",
                                        color = Color(0xFF64748B),
                                    )
                                    Text(
                                        text = "Satıcı: ${l.sellerCompanyName ?: l.sellerName ?: (l.sellerId ?: "-")}",
                                        color = Color(0xFF94A3B8),
                                    )
                                    Text(text = "Fiyat: ${l.price ?: "-"}", color = Color(0xFF64748B))
                                }
                                IconButton(
                                    enabled = sid != null && favSubmittingSellerId != sid,
                                    onClick = { if (sid != null) favSubmittingSellerId = sid },
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
                                    onClick = { detailListingId = l.id },
                                    modifier = Modifier.weight(1f),
                                )
                                FigmaPrimaryButton(
                                    text = "Teklif ver",
                                    onClick = { offerForListing = l },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(favSubmittingSellerId) {
        val sid = favSubmittingSellerId ?: return@LaunchedEffect
        favSubmittingSellerId = null
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
