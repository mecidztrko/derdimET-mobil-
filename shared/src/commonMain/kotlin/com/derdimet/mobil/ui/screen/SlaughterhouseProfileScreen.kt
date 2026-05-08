package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.FavoriteMeatBuyerDto
import com.derdimet.mobil.model.FavoriteSellerDto
import com.derdimet.mobil.model.SlaughterhousePurchaseItemDto
import com.derdimet.mobil.model.SlaughterhouseSaleItemDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SlaughterhouseProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var favoriteSellers by remember { mutableStateOf<List<FavoriteSellerDto>>(emptyList()) }
    var favoriteBuyers by remember { mutableStateOf<List<FavoriteMeatBuyerDto>>(emptyList()) }
    var purchases by remember { mutableStateOf<List<SlaughterhousePurchaseItemDto>>(emptyList()) }
    var sales by remember { mutableStateOf<List<SlaughterhouseSaleItemDto>>(emptyList()) }

    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var refreshTick by remember { mutableStateOf(0) }

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

    val openId = openProfileUserId
    if (openId != null) {
        PublicProfileScreen(
            userId = openId,
            marketService = marketService,
            onBack = { openProfileUserId = null },
            onMessage = { id ->
                openProfileUserId = null
                startChatWithUserId = id
            },
        )
        return
    }

    LaunchedEffect(refreshTick) {
        isLoading = true
        error = null
        val resSellers = marketService.fetchSlaughterhouseFavoriteSellers()
        val resBuyers = marketService.fetchSlaughterhouseFavoriteBuyers()
        val resPurchases = marketService.fetchSlaughterhousePurchases(limit = 10)
        val resSales = marketService.fetchSlaughterhouseSales(limit = 10)

        if (!resSellers.success) error = resSellers.message ?: "Favori satıcılar alınamadı"
        if (!resBuyers.success) error = error ?: (resBuyers.message ?: "Favori alıcılar alınamadı")
        if (!resPurchases.success) error = error ?: (resPurchases.message ?: "Son alımlar alınamadı")
        if (!resSales.success) error = error ?: (resSales.message ?: "Son satımlar alınamadı")

        favoriteSellers = resSellers.data ?: emptyList()
        favoriteBuyers = resBuyers.data ?: emptyList()
        purchases = resPurchases.data ?: emptyList()
        sales = resSales.data ?: emptyList()
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaStyle.ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🏭 Kesimhane Profili", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Favoriler ve son işlem özetleri", color = Color(0xFF94A3B8), fontSize = 12.sp)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Yenile",
                        enabled = !isLoading,
                        onClick = { refreshTick++ },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaSecondaryButton(
                        text = "Çıkış Yap",
                        onClick = onLogout,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Favori satıcılar", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "İlan sayfasındaki kalp ikonundan ekleyebilirsin.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    favoriteSellers.isEmpty() -> Text("Henüz favori satıcı yok.", color = Color(0xFF64748B))
                    else -> favoriteSellers.take(10).forEach { f ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.sellerName ?: "Satıcı #${f.sellerId}", fontWeight = FontWeight.Medium)
                                Text(
                                    text = f.sellerEmail ?: "ID: ${f.sellerId}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            FigmaSecondaryButton(
                                text = "Profil",
                                onClick = { openProfileUserId = f.sellerId },
                            )
                            FigmaSecondaryButton(
                                text = "Sohbet",
                                onClick = { startChatWithUserId = f.sellerId },
                            )
                        }
                    }
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Favori alıcılar", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Et ilanlarına gelen alıcı tekliflerinden de ekleyebilirsin.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    favoriteBuyers.isEmpty() -> Text("Henüz favori alıcı yok.", color = Color(0xFF64748B))
                    else -> favoriteBuyers.take(10).forEach { f ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.buyerName ?: "Alıcı #${f.buyerId}", fontWeight = FontWeight.Medium)
                                Text(
                                    text = f.buyerEmail ?: "ID: ${f.buyerId}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            FigmaSecondaryButton(
                                text = "Profil",
                                onClick = { openProfileUserId = f.buyerId },
                            )
                            FigmaSecondaryButton(
                                text = "Sohbet",
                                onClick = { startChatWithUserId = f.buyerId },
                            )
                        }
                    }
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Son alımlar (hayvan)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    purchases.isEmpty() -> Text("Henüz alım yok.", color = Color(0xFF64748B))
                    else -> purchases.take(10).forEach { p ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = p.requestTitle ?: "Talep", fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${p.sellerName ?: "-"} • ${p.status}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            p.sellerId?.let { id ->
                                FigmaSecondaryButton(text = "Sohbet", onClick = { startChatWithUserId = id })
                            }
                        }
                    }
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Son satımlar (et)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    sales.isEmpty() -> Text("Henüz satış yok.", color = Color(0xFF64748B))
                    else -> sales.take(10).forEach { s ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = s.saleTitle ?: "Et ilanı", fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${s.buyerName ?: "-"} • ${s.status ?: "-"}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            s.buyerId?.let { id ->
                                FigmaSecondaryButton(text = "Sohbet", onClick = { startChatWithUserId = id })
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
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
}
