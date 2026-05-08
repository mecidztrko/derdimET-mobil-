package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
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
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaSegmentedTabs
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.model.BuyerMeatOfferItemDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.service.MarketService

@Composable
fun BuyerMyOffersScreen(
    marketService: MarketService,
) {
    var activeOffersTab by remember { mutableStateOf(true) } // true=offers, false=messages
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var offers by remember { mutableStateOf<List<BuyerMeatOfferItemDto>>(emptyList()) }
    var conversations by remember { mutableStateOf<List<ConversationItemDto>>(emptyList()) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        val resOffers = marketService.fetchMyBuyerMeatOffers()
        val resConvos = marketService.fetchConversations()
        if (resOffers.success) offers = resOffers.data ?: emptyList() else error = resOffers.message ?: "Teklifler alınamadı"
        if (resConvos.success) conversations = resConvos.data ?: emptyList() else error = error ?: (resConvos.message ?: "Mesajlar alınamadı")
        isLoading = false
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
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaStyle.ScreenBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Tekliflerim", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Teklifler ve mesajlar.", color = Color(0xFF94A3B8))

        FigmaSegmentedTabs(
            leftLabel = "Teklifler",
            rightLabel = "Mesajlar",
            selectedLeft = activeOffersTab,
            onLeft = { activeOffersTab = true },
            onRight = { activeOffersTab = false },
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> {
                if (activeOffersTab) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(offers) { item ->
                            FigmaCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = item.title ?: "Et ilanı", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "Teklif: ${item.pricePerKg ?: "-"} • Miktar: ${item.quantity ?: "-"} • Durum: ${item.status}",
                                        color = Color(0xFF64748B),
                                    )
                                    Text(
                                        text = "Kesimhane: ${item.slaughterhouseName ?: item.slaughterhouseId ?: "-"}",
                                        color = Color(0xFF94A3B8),
                                    )
                                }
                            }
                        }
                    }
                } else {
                    if (conversations.isEmpty()) {
                        Text("Henüz mesaj yok.", color = Color.Gray)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
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
                                        FigmaSecondaryButton(
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

