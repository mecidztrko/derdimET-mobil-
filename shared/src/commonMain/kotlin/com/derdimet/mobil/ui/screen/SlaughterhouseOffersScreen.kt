package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.FavoriteMeatBuyerDto
import com.derdimet.mobil.model.FavoriteSellerDto
import com.derdimet.mobil.model.SlaughterhouseListingOfferDto
import com.derdimet.mobil.service.MarketService

@Composable
fun SlaughterhouseOffersScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var offers by remember { mutableStateOf<List<SlaughterhouseListingOfferDto>>(emptyList()) }
    var conversations by remember { mutableStateOf<List<ConversationItemDto>>(emptyList()) }
    var favoriteSellers by remember { mutableStateOf<List<FavoriteSellerDto>>(emptyList()) }
    var favoriteBuyers by remember { mutableStateOf<List<FavoriteMeatBuyerDto>>(emptyList()) }
    var favSubmittingSellerId by remember { mutableStateOf<Long?>(null) }
    var favSubmittingBuyerId by remember { mutableStateOf<Long?>(null) }

    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }

    suspend fun refresh() {
        isLoading = true
        error = null
        val resOffers = marketService.fetchMySlaughterhouseListingOffers()
        val resConvos = marketService.fetchConversations()
        val resFav = marketService.fetchSlaughterhouseFavoriteSellers()
        val resFavBuyers = marketService.fetchSlaughterhouseFavoriteBuyers()
        if (resOffers.success) {
            offers = resOffers.data ?: emptyList()
        } else {
            error = resOffers.message ?: "Teklifler alınamadı"
        }
        if (resConvos.success) {
            conversations = resConvos.data ?: emptyList()
        } else if (error == null) {
            error = resConvos.message ?: "Mesajlar alınamadı"
        }
        if (resFav.success) {
            favoriteSellers = resFav.data ?: emptyList()
        } else if (error == null) {
            error = resFav.message ?: "Favoriler alınamadı"
        }
        if (resFavBuyers.success) {
            favoriteBuyers = resFavBuyers.data ?: emptyList()
        } else if (error == null) {
            error = resFavBuyers.message ?: "Favori alıcılar alınamadı"
        }
        isLoading = false
    }

    LaunchedEffect(Unit) { refresh() }

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
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
                items(offers) { o ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = o.listingType ?: "İlan", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Fiyat: ${o.pricePerKg ?: "-"} • Adet: ${o.quantity ?: "-"} • Durum: ${o.status}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = "Satıcı: ${o.sellerName ?: (o.sellerId?.toString() ?: "-")}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            val sid = o.sellerId
                            val isFav = sid != null && favoriteSellers.any { it.sellerId == sid }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { startChatWithUserId = sid },
                                    enabled = sid != null,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Satıcı ile sohbet")
                                }
                                OutlinedButton(
                                    enabled = sid != null && favSubmittingSellerId != sid,
                                    onClick = { favSubmittingSellerId = sid },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(if (isFav) "Favoriden çıkar" else "Favorile")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(text = "Mesajlar", fontWeight = FontWeight.SemiBold)
        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            conversations.isEmpty() -> Text("Henüz mesaj yok.", color = Color.Gray)
            else -> LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(conversations) { c ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = c.otherUserName ?: (c.otherUserEmail ?: "Kullanıcı #${c.otherUserId}"),
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(text = "Son mesaj: ${c.lastMessageAt ?: "-"}", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                            val otherId = c.otherUserId
                            val otherRole = c.otherUserRole
                            val isMeatBuyer = otherRole == "MEAT_BUYER"
                            val isSeller = otherRole == "ANIMAL_SELLER"
                            val isFavBuyer = isMeatBuyer && favoriteBuyers.any { it.buyerId == otherId }
                            val isFavSeller = isSeller && favoriteSellers.any { it.sellerId == otherId }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { selectedConversation = c },
                                    modifier = Modifier.weight(1f),
                                ) { Text("Sohbet") }
                                if (isMeatBuyer || isSeller) {
                                    OutlinedButton(
                                        enabled =
                                            favSubmittingBuyerId != otherId &&
                                                    favSubmittingSellerId != otherId,
                                        onClick = {
                                            if (isMeatBuyer) favSubmittingBuyerId = otherId
                                            else favSubmittingSellerId = otherId
                                        },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        val isFav = if (isMeatBuyer) isFavBuyer else isFavSeller
                                        Text(if (isFav) "Favoriden çıkar" else "Favorile")
                                    }
                                }
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

        val isFav = favoriteSellers.any { it.sellerId == sid }
        val res = if (isFav) marketService.removeSlaughterhouseFavoriteSeller(sid)
        else marketService.addSlaughterhouseFavoriteSeller(sid)

        if (!res.success) {
            error = res.message ?: "Favori işlemi başarısız"
            return@LaunchedEffect
        }
        val refreshed = marketService.fetchSlaughterhouseFavoriteSellers()
        if (refreshed.success) favoriteSellers = refreshed.data ?: emptyList()
    }

    LaunchedEffect(favSubmittingBuyerId) {
        val bid = favSubmittingBuyerId ?: return@LaunchedEffect
        favSubmittingBuyerId = null

        val isFav = favoriteBuyers.any { it.buyerId == bid }
        val res = if (isFav) marketService.removeSlaughterhouseFavoriteBuyer(bid)
        else marketService.addSlaughterhouseFavoriteBuyer(bid)

        if (!res.success) {
            error = res.message ?: "Favori işlemi başarısız"
            return@LaunchedEffect
        }
        val refreshed = marketService.fetchSlaughterhouseFavoriteBuyers()
        if (refreshed.success) favoriteBuyers = refreshed.data ?: emptyList()
    }
}

