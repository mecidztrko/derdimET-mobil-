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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun BuyerProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var favorites by remember { mutableStateOf<List<FavoriteSellerDto>>(emptyList()) }
    var purchases by remember { mutableStateOf<List<BuyerPurchaseItemDto>>(emptyList()) }

    LaunchedEffect(Unit) {
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Profil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Kişi bilgileri, favoriler ve satın alım geçmişi.",
            color = Color.Gray,
        )

        BuyerProfileSection(
            title = "Kişi bilgileri",
            description = "Detaylar /api/me üzerinden gösterilecek.",
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Favori satıcılar", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    favorites.isEmpty() -> Text("Henüz favori satıcın yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> favorites.take(10).forEach { f ->
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text(text = f.sellerName ?: (f.sellerEmail ?: f.sellerId.toString()), fontWeight = FontWeight.Medium)
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
                Text(text = "Son satın alımlar", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    purchases.isEmpty() -> Text("Henüz satın alım yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> purchases.take(10).forEach { p ->
                        Text(
                            text = "Sipariş #${p.orderId} • ${p.status} • ${p.totalPrice ?: "-"}",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Çıkış Yap")
        }
    }
}

@Composable
private fun BuyerProfileSection(
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = description, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

