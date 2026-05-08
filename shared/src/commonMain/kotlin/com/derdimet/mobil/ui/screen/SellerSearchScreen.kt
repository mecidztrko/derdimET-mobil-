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
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.CreateAnimalOfferPayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

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
    var requests by remember { mutableStateOf<List<AnimalPurchaseRequestDto>>(emptyList()) }
    var favoriteSlaughterhouseIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var favSubmittingId by remember { mutableStateOf<Long?>(null) }

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(SellerReqFilters()) }
    var filterOpen by remember { mutableStateOf(false) }
    var refreshTick by remember { mutableStateOf(0) }

    var detailRequestId by remember { mutableStateOf<Long?>(null) }
    var offerForRequest by remember { mutableStateOf<AnimalPurchaseRequestDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    suspend fun refreshFavorites() {
        val fav = marketService.fetchSellerFavoriteBuyers()
        if (fav.success) {
            favoriteSlaughterhouseIds = (fav.data ?: emptyList()).map { it.buyerId }.toSet()
        }
    }

    LaunchedEffect(query, filters, refreshTick) {
        isLoading = true
        error = null
        val res = marketService.fetchOpenAnimalPurchaseRequestsFiltered(
            category = filters.category?.name,
            q = query.takeIf { it.isNotBlank() },
            sort = filters.sort,
        )
        if (res.success) requests = res.data ?: emptyList() else error = res.message ?: "İlanlar alınamadı"
        isLoading = false
    }

    LaunchedEffect(Unit) { refreshFavorites() }

    val detailId = detailRequestId
    if (detailId != null) {
        AnimalPurchaseRequestDetailScreen(
            requestId = detailId,
            marketService = marketService,
            onBack = { detailRequestId = null },
            onMakeOffer = { item -> offerForRequest = item },
            onMessage = { sid ->
                detailRequestId = null
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
                    detailRequestId = null
                    startChatWithUserId = id
                },
            )
        }
        return
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
                detailRequestId = null
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
                        Text(text = "🔎 Kesimhane Talepleri", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Kesimhanelerin açtığı alım talepleri", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Text(
                        text = "${requests.size} ilan",
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
                        label = { Text("Ara (başlık, açıklama)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedButton(onClick = { filterOpen = true }) {
                        Text(if (filters.category != null) "Filtre (1)" else "Filtre")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (filters.category != null) {
                        TextButton(
                            onClick = { filters = filters.copy(category = null) },
                            modifier = Modifier.background(Color(0xFF1B3A6B).copy(alpha = 0.10f), RoundedCornerShape(999.dp)),
                        ) {
                            Text("Kategori: ${filters.category!!.name}  ✕", color = Color(0xFF1B3A6B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (filters.sort != "newest") {
                        TextButton(
                            onClick = { filters = filters.copy(sort = "newest") },
                            modifier = Modifier.background(Color(0xFF1B3A6B).copy(alpha = 0.10f), RoundedCornerShape(999.dp)),
                        ) {
                            Text("Sıralama  ✕", color = Color(0xFF1B3A6B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(requests) { r ->
                    val sid = r.slaughterhouseId
                    val isFav = sid != null && favoriteSlaughterhouseIds.contains(sid)
                    FigmaCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { detailRequestId = r.id.toLong() },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = r.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "Kategori: ${r.animalCategory ?: "-"} • Adet: ${r.quantity ?: "-"}",
                                        color = Color(0xFF64748B),
                                    )
                                    Text(
                                        text = "Kesimhane: ${r.slaughterhouseCompanyName ?: r.slaughterhouseName ?: "-"}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                    )
                                    r.description?.takeIf { it.isNotBlank() }?.let {
                                        Text(text = it, color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    }
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
                                    onClick = { detailRequestId = r.id.toLong() },
                                    modifier = Modifier.weight(1f),
                                )
                                FigmaPrimaryButton(
                                    text = "Teklif ver",
                                    onClick = { offerForRequest = r },
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
