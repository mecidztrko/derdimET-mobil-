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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.FavoriteBuyerDto
import com.derdimet.mobil.model.SellerSaleItemDto
import com.derdimet.mobil.service.MarketService

@Composable
fun SellerProfileScreen(
    marketService: MarketService,
    onLogout: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var favorites by remember { mutableStateOf<List<FavoriteBuyerDto>>(emptyList()) }
    var sales by remember { mutableStateOf<List<SellerSaleItemDto>>(emptyList()) }

    LaunchedEffect(Unit) {
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
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Profil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Kişi bilgileri, favori alıcılar ve son satımlar.", color = Color.Gray)

        InfoCard(title = "Kişi bilgileri", description = "/api/me üzerinden gösterilecek.")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Favori alıcılar (Kesimhaneler)", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    favorites.isEmpty() -> Text("Henüz favori kesimhane yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> favorites.take(10).forEach { f ->
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text(text = f.buyerName ?: (f.buyerEmail ?: f.buyerId.toString()), fontWeight = FontWeight.Medium)
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
                Text(text = "Son satımlar", fontWeight = FontWeight.SemiBold)
                when {
                    isLoading -> Text("Yükleniyor...", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
                    sales.isEmpty() -> Text("Henüz satış yok.", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    else -> sales.take(10).forEach { s ->
                        Text(
                            text = "${s.requestTitle ?: "İlan"} • ${s.status} • ${s.slaughterhouseName ?: "-"}",
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
        ) { Text("Çıkış Yap") }
    }
}

@Composable
private fun InfoCard(title: String, description: String) {
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

