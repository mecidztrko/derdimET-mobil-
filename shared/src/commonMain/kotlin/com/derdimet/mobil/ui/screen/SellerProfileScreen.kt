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
import com.derdimet.mobil.model.FavoriteBuyerDto
import com.derdimet.mobil.model.SellerSaleItemDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SellerProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var favorites by remember { mutableStateOf<List<FavoriteBuyerDto>>(emptyList()) }
    var sales by remember { mutableStateOf<List<SellerSaleItemDto>>(emptyList()) }
    var refreshTick by remember { mutableStateOf(0) }

    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

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
        val fav = marketService.fetchSellerFavoriteBuyers()
        val sls = marketService.fetchSellerSales(limit = 10)
        if (!fav.success) error = fav.message ?: "Favoriler alınamadı"
        if (!sls.success) error = error ?: (sls.message ?: "Satışlar alınamadı")
        favorites = fav.data ?: emptyList()
        sales = sls.data ?: emptyList()
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
                Text(text = "👤 Profil", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Favori kesimhaneler ve son satışlar", color = FigmaStyle.MutedText, fontSize = 12.sp)
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
                        enabled = true,
                        onClick = onLogout,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Favori alıcılar (Kesimhaneler)", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "İlan sayfasındaki kalp ikonundan ekleyebilirsin.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    favorites.isEmpty() -> Text("Henüz favori kesimhane yok.", color = Color(0xFF64748B))
                    else -> favorites.take(10).forEach { f ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.buyerName ?: "Kesimhane #${f.buyerId}", fontWeight = FontWeight.Medium)
                                Text(
                                    text = f.buyerEmail ?: "ID: ${f.buyerId}",
                                    color = FigmaStyle.MutedText,
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
                Text(text = "Son satımlar", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    sales.isEmpty() -> Text("Henüz satış yok.", color = Color(0xFF64748B))
                    else -> sales.take(10).forEach { s ->
                        Text(
                            text = "${s.requestTitle ?: "İlan"} • ${s.status} • ${s.slaughterhouseName ?: "-"}",
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp),
                        )
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

