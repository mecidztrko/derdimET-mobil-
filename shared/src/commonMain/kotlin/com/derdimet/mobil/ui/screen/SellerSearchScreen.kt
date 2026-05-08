package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

private data class SellerReqFilters(
    val sort: String = "newest",
    val category: AnimalCategory? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var requests by remember { mutableStateOf<List<AnimalPurchaseRequestDto>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(SellerReqFilters()) }
    var filterOpen by remember { mutableStateOf(false) }
    var refreshTick by remember { mutableStateOf(0) }

    LaunchedEffect(query, filters, refreshTick) {
        isLoading = true
        error = null
        val res = marketService.fetchOpenAnimalPurchaseRequestsFiltered(
            category = filters.category?.name,
            q = query.takeIf { it.isNotBlank() },
            sort = filters.sort,
        )
        if (res.success) requests = res.data ?: emptyList() else error = res.message ?: "İlanlar alınamadı"
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaStyle.ScreenBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(text = "🔎 Kesimhane Talepleri", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Kesimhanelerin açtığı alım talepleri", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Text(
                        text = "${requests.size} ilan",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), androidx.compose.foundation.shape.RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Ara (başlık, açıklama)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedButton(onClick = { filterOpen = true }) {
                        Text(if (filters.category != null) "Filtre (1)" else "Filtre")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (filters.category != null) {
                        TextButton(
                            onClick = { filters = filters.copy(category = null) },
                            modifier = Modifier.background(Color(0xFF1B3A6B).copy(alpha = 0.10f), androidx.compose.foundation.shape.RoundedCornerShape(999.dp)),
                        ) {
                            Text("Kategori: ${filters.category!!.name}  ✕", color = Color(0xFF1B3A6B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (filters.sort != "newest") {
                        TextButton(
                            onClick = { filters = filters.copy(sort = "newest") },
                            modifier = Modifier.background(Color(0xFF1B3A6B).copy(alpha = 0.10f), androidx.compose.foundation.shape.RoundedCornerShape(999.dp)),
                        ) {
                            Text("Sıralama  ✕", color = Color(0xFF1B3A6B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Yenile",
                        enabled = !isLoading,
                        onClick = { refreshTick++ },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaSecondaryButton(
                        text = "Filtreler",
                        onClick = { filterOpen = true },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(requests) { r ->
                    FigmaCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = r.title, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Kategori: ${r.animalCategory ?: "-"} • Adet: ${r.quantity ?: "-"}",
                                color = Color(0xFF64748B),
                            )
                            r.description?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (filterOpen) {
        ModalBottomSheet(
            onDismissRequest = { filterOpen = false },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "Filtrele", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text(text = "Sıralama", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FigmaSecondaryButton("En yeni", onClick = { filters = filters.copy(sort = "newest") }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Eski", onClick = { filters = filters.copy(sort = "oldest") }, modifier = Modifier.weight(1f))
                }

                Text(text = "Kategori", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FigmaSecondaryButton("Tümü", onClick = { filters = filters.copy(category = null) }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Küçükbaş", onClick = { filters = filters.copy(category = AnimalCategory.KUCUKBAS) }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Büyükbaş", onClick = { filters = filters.copy(category = AnimalCategory.BUYUKBAS) }, modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Sıfırla",
                        onClick = { filters = SellerReqFilters() },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaPrimaryButton(
                        text = "Uygula",
                        onClick = { filterOpen = false },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

