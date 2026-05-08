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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.derdimet.mobil.model.FavoriteMeatBuyerDto
import com.derdimet.mobil.model.FavoriteSellerDto
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.SlaughterhousePurchaseItemDto
import com.derdimet.mobil.model.SlaughterhouseSaleItemDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
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

    var addSellerIdText by remember { mutableStateOf("") }
    var addBuyerIdText by remember { mutableStateOf("") }
    var actionSubmitting by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }

    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var startChatWithUserId by remember { mutableStateOf<Long?>(null) }
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

    LaunchedEffect(refreshTick) { refresh() }

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
                if (isLoading) {
                    Text(text = "Yükleniyor...", color = Color(0xFF64748B))
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                actionError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FigmaSecondaryButton(
                        text = "Yenile",
                        enabled = !isLoading,
                        onClick = { refreshTick++ },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaSecondaryButton(
                        text = "Çıkış Yap",
                        enabled = !actionSubmitting,
                        onClick = onLogout,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Favori satıcılar
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Favori satıcılar", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = addSellerIdText,
                        onValueChange = { addSellerIdText = it },
                        label = { Text("Satıcı ID") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    FigmaPrimaryButton(
                        text = if (actionSubmitting) "..." else "Ekle",
                        enabled = !actionSubmitting,
                        onClick = { actionSubmitting = true },
                    )
                }

                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    favoriteSellers.isEmpty() -> Text("Henüz favori satıcı yok.", color = Color(0xFF64748B))
                    else -> favoriteSellers.take(10).forEach { f ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.sellerName ?: "Satıcı #${f.sellerId}", fontWeight = FontWeight.Medium)
                                Text(
                                    text = f.sellerEmail ?: "ID: ${f.sellerId}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            FigmaSecondaryButton(
                                text = "Kaldır",
                                enabled = !actionSubmitting,
                                onClick = {
                                    addSellerIdText = f.sellerId.toString()
                                    addBuyerIdText = ""
                                    actionSubmitting = true
                                },
                            )
                        }
                    }
                }
            }
        }

        // Favori alıcılar
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Favori alıcılar", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = addBuyerIdText,
                        onValueChange = { addBuyerIdText = it },
                        label = { Text("Alıcı ID") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    FigmaPrimaryButton(
                        text = if (actionSubmitting) "..." else "Ekle",
                        enabled = !actionSubmitting,
                        onClick = { actionSubmitting = true },
                    )
                }

                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    favoriteBuyers.isEmpty() -> Text("Henüz favori alıcı yok.", color = Color(0xFF64748B))
                    else -> favoriteBuyers.take(10).forEach { f ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.buyerName ?: "Alıcı #${f.buyerId}", fontWeight = FontWeight.Medium)
                                Text(
                                    text = f.buyerEmail ?: "ID: ${f.buyerId}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            FigmaSecondaryButton(
                                text = "Kaldır",
                                enabled = !actionSubmitting,
                                onClick = {
                                    addBuyerIdText = f.buyerId.toString()
                                    addSellerIdText = ""
                                    actionSubmitting = true
                                },
                            )
                        }
                    }
                }
            }
        }

        // Son alımlar
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Son alımlar (hayvan)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    purchases.isEmpty() -> Text("Henüz alım yok.", color = Color(0xFF64748B))
                    else -> purchases.take(10).forEach { p ->
                        val sid = p.sellerId
                        val isFavSeller = sid != null && favoriteSellers.any { it.sellerId == sid }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = p.requestTitle ?: "Talep", fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${p.sellerName ?: "-"} • ${p.status}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            FigmaSecondaryButton(
                                text = if (isFavSeller) "Favoriden çıkar" else "Favorile",
                                enabled = sid != null && !actionSubmitting,
                                onClick = {
                                    addBuyerIdText = ""
                                    addSellerIdText = sid.toString()
                                    actionSubmitting = true
                                },
                            )
                            FigmaSecondaryButton(
                                text = "Sohbet",
                                enabled = sid != null,
                                onClick = { if (sid != null) startChatWithUserId = sid },
                            )
                        }
                    }
                }
            }
        }

        // Son satımlar
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Son satımlar (et)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    sales.isEmpty() -> Text("Henüz satış yok.", color = Color(0xFF64748B))
                    else -> sales.take(10).forEach { s ->
                        val bid = s.buyerId
                        val isFavBuyer = bid != null && favoriteBuyers.any { it.buyerId == bid }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = s.saleTitle ?: "Et ilanı", fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${s.buyerName ?: "-"} • ${s.status ?: "-"}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                            FigmaSecondaryButton(
                                text = if (isFavBuyer) "Favoriden çıkar" else "Favorile",
                                enabled = bid != null && !actionSubmitting,
                                onClick = {
                                    addSellerIdText = ""
                                    addBuyerIdText = bid.toString()
                                    actionSubmitting = true
                                },
                            )
                            FigmaSecondaryButton(
                                text = "Sohbet",
                                enabled = bid != null,
                                onClick = { if (bid != null) startChatWithUserId = bid },
                            )
                        }
                    }
                }
            }
        }
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

