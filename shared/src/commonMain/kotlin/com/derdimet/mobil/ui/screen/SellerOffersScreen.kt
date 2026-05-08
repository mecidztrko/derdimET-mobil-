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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.derdimet.mobil.model.SellerAnimalOfferItemDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaSegmentedTabs
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SellerOffersScreen(
    marketService: MarketService,
) {
    var activeOffersTab by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var offers by remember { mutableStateOf<List<SellerAnimalOfferItemDto>>(emptyList()) }
    var conversations by remember { mutableStateOf<List<ConversationItemDto>>(emptyList()) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
    var refreshTick by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTick) {
        isLoading = true
        error = null
        val resOffers = marketService.fetchMyAnimalOffers()
        val resConvos = marketService.fetchConversations()
        if (resOffers.success) offers = resOffers.data ?: emptyList() else error = resOffers.message ?: "Teklifler alınamadı"
        if (resConvos.success) conversations = resConvos.data ?: emptyList() else error = error ?: (resConvos.message ?: "Mesajlar alınamadı")
        isLoading = false
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "💬 Teklifler & Mesajlar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Kesimhanelerle yaptığın görüşmeler", color = Color(0xFF94A3B8), fontSize = 12.sp)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Yenile",
                        enabled = !isLoading,
                        onClick = { refreshTick++ },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaSecondaryButton(
                        text = if (activeOffersTab) "Teklifler" else "Mesajlar",
                        onClick = { activeOffersTab = !activeOffersTab },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        FigmaSegmentedTabs(
            leftLabel = "Teklifler",
            rightLabel = "Mesajlar",
            selectedLeft = activeOffersTab,
            onLeft = { activeOffersTab = true },
            onRight = { activeOffersTab = false },
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> {
                if (activeOffersTab) {
                    LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(offers) { o ->
                            FigmaCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = o.request.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "Fiyat: ${o.pricePerKg} ₺/kg • Durum: ${o.status}",
                                        color = Color(0xFF64748B),
                                    )
                                    Text(
                                        text = "Kesimhane: ${o.request.slaughterhouseName ?: (o.request.slaughterhouseId ?: "-")}",
                                        color = Color(0xFF94A3B8),
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        FigmaPrimaryButton(
                                            text = "Sohbet",
                                            onClick = {
                                                val otherId = o.request.slaughterhouseId
                                                if (otherId != null) startChatWithUserId = otherId
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (conversations.isEmpty()) {
                        Text("Henüz mesaj yok.", color = Color(0xFF64748B))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(conversations) { c ->
                                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = c.otherUserName ?: (c.otherUserEmail ?: "Kullanıcı #${c.otherUserId}"),
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = "Son mesaj: ${c.lastMessageAt ?: "-"}",
                                            color = Color(0xFF94A3B8),
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        FigmaPrimaryButton(
                                            text = "Sohbet",
                                            onClick = { selectedConversation = c },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

