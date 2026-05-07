package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
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
import com.derdimet.mobil.model.FavoriteMeatBuyerDto
import com.derdimet.mobil.model.FavoriteSellerDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.SlaughterhousePurchaseItemDto
import com.derdimet.mobil.model.SlaughterhouseSaleItemDto
import com.derdimet.mobil.service.MarketService

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

    var addSellerIdText by remember { mutableStateOf("") }
    var addBuyerIdText by remember { mutableStateOf("") }
    var actionSubmitting by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }

    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }

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

    suspend fun refreshFavoriteSellers() {
        val res = marketService.fetchSlaughterhouseFavoriteSellers()
        if (res.success) {
            favoriteSellers = res.data ?: emptyList()
        } else if (error == null) {
            error = res.message ?: "Favori satıcılar alınamadı"
        }
    }

    suspend fun refreshFavoriteBuyers() {
        val res = marketService.fetchSlaughterhouseFavoriteBuyers()
        if (res.success) {
            favoriteBuyers = res.data ?: emptyList()
        } else if (error == null) {
            error = res.message ?: "Favori alıcılar alınamadı"
        }
    }

    suspend fun refresh() {
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

    LaunchedEffect(Unit) { refresh() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Profil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Kişi bilgileri, favoriler ve son işlem özetleri.", color = Color.Gray)

        actionError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Favori satıcılar", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = addSellerIdText,
                        onValueChange = { addSellerIdText = it },
                        label = { Text("Satıcı ID") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedButton(
                        enabled = !actionSubmitting,
                        onClick = { actionSubmitting = true },
                    ) { Text("Ekle") }
                }
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    favoriteSellers.isEmpty() -> Text("Henüz favori satıcı yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> favoriteSellers.take(10).forEach { f ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = f.sellerName ?: (f.sellerEmail ?: f.sellerId.toString()),
                                color = Color.Gray,
                            )
                            OutlinedButton(
                                enabled = !actionSubmitting,
                                onClick = {
                                    addSellerIdText = f.sellerId.toString()
                                    actionSubmitting = true
                                },
                            ) { Text("Kaldır") }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Favori alıcılar", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = addBuyerIdText,
                        onValueChange = { addBuyerIdText = it },
                        label = { Text("Alıcı ID") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedButton(
                        enabled = !actionSubmitting,
                        onClick = { actionSubmitting = true },
                    ) { Text("Ekle") }
                }
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    favoriteBuyers.isEmpty() -> Text("Henüz favori alıcı yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> favoriteBuyers.take(10).forEach { f ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = f.buyerName ?: (f.buyerEmail ?: f.buyerId.toString()),
                                color = Color.Gray,
                            )
                            OutlinedButton(
                                enabled = !actionSubmitting,
                                onClick = {
                                    addBuyerIdText = f.buyerId.toString()
                                    actionSubmitting = true
                                },
                            ) { Text("Kaldır") }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Son alımlar (hayvan)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    purchases.isEmpty() -> Text("Henüz alım yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> purchases.take(10).forEach { p ->
                        val sid = p.sellerId
                        val isFavSeller = sid != null && favoriteSellers.any { it.sellerId == sid }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "${p.requestTitle ?: "Talep"} • ${p.sellerName ?: "-"} • ${p.status}",
                                color = Color.Gray,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedButton(
                                enabled = sid != null && !actionSubmitting,
                                onClick = {
                                    addBuyerIdText = ""
                                    addSellerIdText = sid.toString()
                                    actionSubmitting = true
                                },
                            ) { Text(if (isFavSeller) "Favoriden çıkar" else "Favorile") }
                            OutlinedButton(
                                enabled = sid != null,
                                onClick = { startChatWithUserId = sid },
                            ) { Text("Sohbet") }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Son satımlar (et)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    sales.isEmpty() -> Text("Henüz satış yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> sales.take(10).forEach { s ->
                        val bid = s.buyerId
                        val isFavBuyer = bid != null && favoriteBuyers.any { it.buyerId == bid }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${s.saleTitle ?: "Et ilanı"} • ${s.buyerName ?: "-"} • ${s.status ?: "-"}",
                                color = Color.Gray,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedButton(
                                enabled = bid != null && !actionSubmitting,
                                onClick = {
                                    addSellerIdText = ""
                                    addBuyerIdText = bid.toString()
                                    actionSubmitting = true
                                },
                            ) {
                                Text(if (isFavBuyer) "Favoriden çıkar" else "Favorile")
                            }
                            OutlinedButton(
                                enabled = bid != null,
                                onClick = { startChatWithUserId = bid },
                                modifier = Modifier.padding(start = 8.dp),
                            ) { Text("Sohbet") }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) { Text("Çıkış Yap") }
    }

    LaunchedEffect(actionSubmitting) {
        if (!actionSubmitting) return@LaunchedEffect
        actionError = null

        // Seller favorite action: if seller id text is set, toggle remove/add by presence in list
        val sellerId = addSellerIdText.trim().toLongOrNull()
        val buyerId = addBuyerIdText.trim().toLongOrNull()

        if (sellerId == null && buyerId == null) {
            actionError = "Bir Satıcı ID veya Alıcı ID girin."
            actionSubmitting = false
            return@LaunchedEffect
        }

        // If both are filled, prioritize seller (simple UX).
        val res = if (sellerId != null) {
            val alreadyFav = favoriteSellers.any { it.sellerId == sellerId }
            if (alreadyFav) marketService.removeSlaughterhouseFavoriteSeller(sellerId)
            else marketService.addSlaughterhouseFavoriteSeller(sellerId)
        } else {
            val id = buyerId!!
            val alreadyFav = favoriteBuyers.any { it.buyerId == id }
            if (alreadyFav) marketService.removeSlaughterhouseFavoriteBuyer(id)
            else marketService.addSlaughterhouseFavoriteBuyer(id)
        }

        if (!res.success) {
            actionError = res.message ?: "İşlem başarısız"
            actionSubmitting = false
            return@LaunchedEffect
        }

        addSellerIdText = ""
        addBuyerIdText = ""
        actionSubmitting = false
        if (sellerId != null) {
            refreshFavoriteSellers()
        } else {
            refreshFavoriteBuyers()
        }
    }

    LaunchedEffect(startChatWithUserId) {
        val otherId = startChatWithUserId ?: return@LaunchedEffect
        startChatWithUserId = null
        val res = marketService.getOrCreateConversation(otherId)
        if (res.success && res.data != null) {
            selectedConversation = res.data
        } else {
            actionError = res.message ?: "Sohbet başlatılamadı"
        }
    }
}

