package com.derdimet.mobil.ui.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.service.MarketService

@Composable
fun SellerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var requests by remember { mutableStateOf<List<AnimalPurchaseRequestDto>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<AnimalCategory?>(null) }

    LaunchedEffect(query, category) {
        isLoading = true
        error = null
        val res = marketService.fetchOpenAnimalPurchaseRequestsFiltered(
            category = category?.name,
            q = query.takeIf { it.isNotBlank() },
            sort = "newest",
        )
        if (res.success) requests = res.data ?: emptyList() else error = res.message ?: "İlanlar alınamadı"
        isLoading = false
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Arama", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Kesimhanelerin açtığı açık ilanları inceleyin.", color = Color.Gray)

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Ara (başlık)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { category = null },
                modifier = Modifier.weight(1f)
            ) { Text("Tümü") }
            OutlinedButton(
                onClick = { category = AnimalCategory.KUCUKBAS },
                modifier = Modifier.weight(1f)
            ) { Text("Küçükbaş") }
            OutlinedButton(
                onClick = { category = AnimalCategory.BUYUKBAS },
                modifier = Modifier.weight(1f)
            ) { Text("Büyükbaş") }
        }
        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(requests) { r ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = r.title, fontWeight = FontWeight.SemiBold)
                            Text(text = "Kategori: ${r.animalCategory ?: "-"} • Adet: ${r.quantity ?: "-"}", color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

