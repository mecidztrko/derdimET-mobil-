package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.SellerAnimalOfferItemDto
import com.derdimet.mobil.repository.AnimalCategoryFilter
import com.derdimet.mobil.ui.components.DashboardEmptyState
import com.derdimet.mobil.ui.components.DashboardInlineMessage
import com.derdimet.mobil.ui.components.DashboardLoadingState
import com.derdimet.mobil.ui.components.DashboardStatusBadge
import com.derdimet.mobil.ui.components.StatusTone
import com.derdimet.mobil.viewmodel.SellerViewModel

@Composable
fun SellerHomeScreen(
    viewModel: SellerViewModel,
    selectedFilter: AnimalCategoryFilter
) {
    val requests by viewModel.requests.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedRequest by remember { mutableStateOf<AnimalPurchaseRequestDto?>(null) }
    val filteredRequests = remember(requests, selectedFilter) {
        when (selectedFilter) {
            AnimalCategoryFilter.ALL -> requests
            AnimalCategoryFilter.KUCUKBAS -> requests.filter { it.animalCategory?.name == AnimalCategoryFilter.KUCUKBAS.name }
            AnimalCategoryFilter.BUYUKBAS -> requests.filter { it.animalCategory?.name == AnimalCategoryFilter.BUYUKBAS.name }
        }
    }
    val filteredOffers = remember(offers, selectedFilter) {
        when (selectedFilter) {
            AnimalCategoryFilter.ALL -> offers
            AnimalCategoryFilter.KUCUKBAS -> offers.filter { it.request.animalCategory?.name == AnimalCategoryFilter.KUCUKBAS.name }
            AnimalCategoryFilter.BUYUKBAS -> offers.filter { it.request.animalCategory?.name == AnimalCategoryFilter.BUYUKBAS.name }
        }
    }

    if (isLoading && filteredRequests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.TopCenter) {
            DashboardLoadingState("İlanlar yükleniyor...")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            item {
                Text(text = "Satıcı Paneli", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = "Açık talepleri inceleyip hızlıca teklif verin.",
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                    color = Color.Gray
                )
                SellerStatsRow(
                    openRequestCount = filteredRequests.size,
                    offerCount = filteredOffers.size
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            error?.let {
                item {
                    DashboardInlineMessage(
                        text = it,
                        tone = StatusTone.Danger,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            item {
                Text(text = "Açık ilanlar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            }

            if (filteredRequests.isEmpty()) {
                item {
                    DashboardEmptyState(
                        title = "Açık ilan yok",
                        description = "Yönetici yeni talep açtığında burada listelenecek."
                    )
                }
            } else {
                items(filteredRequests) { request ->
                    RequestCard(request, onOfferClick = { selectedRequest = request })
                }
            }

            item {
                Text(text = "Verdiğim teklifler", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 28.dp, bottom = 8.dp))
            }

            if (filteredOffers.isEmpty()) {
                item {
                    DashboardEmptyState(
                        title = "Henüz teklif yok",
                        description = "Açık ilanlardan teklif vererek bu alanı doldurabilirsiniz."
                    )
                }
            } else {
                items(filteredOffers) { offer ->
                    OfferCard(offer)
                }
            }
        }
    }

    selectedRequest?.let { request ->
        OfferDialog(
            title = request.title,
            onDismiss = { selectedRequest = null },
            onSubmit = { price, count, note ->
                viewModel.submitOffer(
                    requestId = request.id,
                    price = price,
                    count = count,
                    note = note
                ) {
                    selectedRequest = null
                }
            }
        )
    }
}

@Composable
fun RequestCard(request: AnimalPurchaseRequestDto, onOfferClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = request.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                DashboardStatusBadge(text = requestStatusText(request.status.name), tone = statusToneForRequest(request.status.name))
            }
            Text(text = "Tür: ${requestCategoryText(request.animalCategory?.name)}", style = MaterialTheme.typography.bodySmall)
            request.quantity?.let { Text(text = "Adet: $it", style = MaterialTheme.typography.bodySmall) }
            request.expectedWeight?.let { Text(text = "Beklenen ağırlık: $it kg", style = MaterialTheme.typography.bodySmall) }
            request.description?.let { Text(text = it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
            
            Button(
                onClick = onOfferClick,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Teklif ver")
            }
        }
    }
}

@Composable
fun OfferCard(offer: SellerAnimalOfferItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = offer.request.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            Text(text = "${offer.pricePerKg} ₺/kg", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Durum", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                DashboardStatusBadge(text = offerStatusText(offer.status.name), tone = statusToneForOffer(offer.status.name))
            }
            Text(text = offer.createdAt, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun SellerStatsRow(openRequestCount: Int, offerCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(label = "Açık ilan", value = openRequestCount.toString(), modifier = Modifier.weight(1f))
        StatCard(label = "Teklif", value = offerCount.toString(), modifier = Modifier.weight(1f))
    }
}

private fun statusToneForRequest(status: String): StatusTone {
    return when (status.uppercase()) {
        "OPEN" -> StatusTone.Info
        "CLOSED" -> StatusTone.Neutral
        else -> StatusTone.Neutral
    }
}

private fun statusToneForOffer(status: String): StatusTone {
    return when (status.uppercase()) {
        "ACCEPTED" -> StatusTone.Success
        "REJECTED" -> StatusTone.Danger
        "PENDING" -> StatusTone.Warning
        else -> StatusTone.Neutral
    }
}

private fun requestStatusText(status: String): String {
    return when (status.uppercase()) {
        "OPEN" -> "Açık"
        "CLOSED" -> "Kapalı"
        else -> status
    }
}

private fun offerStatusText(status: String): String {
    return when (status.uppercase()) {
        "PENDING" -> "Beklemede"
        "ACCEPTED" -> "Kabul edildi"
        "REJECTED" -> "Reddedildi"
        else -> status
    }
}

private fun requestCategoryText(category: String?): String {
    return when (category?.uppercase()) {
        "KUCUKBAS" -> "Küçükbaş"
        "BUYUKBAS" -> "Büyükbaş"
        else -> "Belirtilmemiş"
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OfferDialog(
    title: String,
    onDismiss: () -> Unit,
    onSubmit: (price: Double, count: Int?, note: String?) -> Unit
) {
    var pricePerKg by remember { mutableStateOf("") }
    var animalCount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Teklif ver") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                OutlinedTextField(
                    value = pricePerKg,
                    onValueChange = { pricePerKg = it },
                    label = { Text("Fiyat (₺/kg) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = animalCount,
                    onValueChange = { animalCount = it },
                    label = { Text("Adet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Not") },
                    modifier = Modifier.fillMaxWidth()
                )
                localError?.let {
                    Text(text = it, color = Color(0xFFB91C1C), style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val price = pricePerKg.toDoubleOrNull()
                if (price == null || price <= 0.0) {
                    localError = "Geçerli bir fiyat girin"
                    return@TextButton
                }
                val count = animalCount.toIntOrNull()
                onSubmit(price, count, note.takeIf { it.isNotBlank() })
            }) {
                Text("Gönder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}
