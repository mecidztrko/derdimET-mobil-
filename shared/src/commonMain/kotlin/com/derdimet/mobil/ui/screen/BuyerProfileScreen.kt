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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.BuyerPurchaseItemDto
import com.derdimet.mobil.model.FavoriteSellerDto
import com.derdimet.mobil.service.MarketService
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun BuyerProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var favorites by remember { mutableStateOf<List<FavoriteSellerDto>>(emptyList()) }
    var purchases by remember { mutableStateOf<List<BuyerPurchaseItemDto>>(emptyList()) }
    var refreshTick by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTick) {
        isLoading = true
        error = null

        val fav = marketService.fetchFavoriteSellers()
        val pur = marketService.fetchMyPurchases(limit = 10)

        if (!fav.success) error = fav.message ?: "Favoriler alınamadı"
        if (!pur.success) error = error ?: (pur.message ?: "Satın alımlar alınamadı")

        favorites = fav.data ?: emptyList()
        purchases = pur.data ?: emptyList()
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
                Text(text = "Favoriler ve satın alım geçmişi", color = FigmaStyle.MutedText, fontSize = 12.sp)
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
                Text(text = "Favori satıcılar", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    favorites.isEmpty() -> Text("Henüz favori satıcın yok.", color = Color(0xFF64748B))
                    else -> favorites.take(10).forEach { f ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = f.sellerName ?: "Satıcı #${f.sellerId}", fontWeight = FontWeight.Medium)
                                Text(
                                    text = f.sellerEmail ?: "ID: ${f.sellerId}",
                                    color = FigmaStyle.MutedText,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Son satın alımlar", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    purchases.isEmpty() -> Text("Henüz satın alım yok.", color = Color(0xFF64748B))
                    else -> purchases.take(10).forEach { p ->
                        Text(
                            text = "Sipariş #${p.orderId} • ${p.status} • ${p.totalPrice ?: "-"}",
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

