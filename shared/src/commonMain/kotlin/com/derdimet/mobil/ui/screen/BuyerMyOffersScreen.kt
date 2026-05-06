package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.derdimet.mobil.model.BuyerMeatOfferItemDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.service.MarketService

@Composable
fun BuyerMyOffersScreen(
    marketService: MarketService,
) {
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Tekliflerim", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Verdiğiniz teklifler ve mesajlar.", color = Color.Gray)

        Text(text = "Teklifler", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(offers) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = item.title ?: "Et ilanı",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Teklif: ${item.pricePerKg ?: "-"} • Miktar: ${item.quantity ?: "-"} • Durum: ${item.status}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Text(
                                text = "Kesimhane: ${item.slaughterhouseName ?: item.slaughterhouseId ?: "-"}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(text = "Mesajlar", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp))
        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            conversations.isEmpty() -> Text("Henüz mesaj yok.", color = Color.Gray)
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(conversations) { c ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = c.otherUserName ?: (c.otherUserEmail ?: "Kullanıcı #${c.otherUserId}"), fontWeight = FontWeight.SemiBold)
                            Text(text = "Son mesaj: ${c.lastMessageAt ?: "-"}", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                            OutlinedButton(onClick = { selectedConversation = c }, modifier = Modifier.padding(top = 10.dp)) {
                                Text("Sohbet")
                            }
                        }
                    }
                }
            }
        }
    }
}

