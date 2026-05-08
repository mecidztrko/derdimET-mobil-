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
import androidx.compose.material3.AlertDialog
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
import com.derdimet.mobil.model.CreateMeatOfferPayload
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

private data class BuyerSearchFilters(
    val sort: String = "newest", // newest | qtydesc | qtyasc
    val meatType: String = "",
    val quantityMin: String = "",
    val quantityMax: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<MeatSaleRequestDto>>(emptyList()) }

    var query by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(BuyerSearchFilters()) }
    var filterOpen by remember { mutableStateOf(false) }

    var offerDialogListing by remember { mutableStateOf<MeatSaleRequestDto?>(null) }
    var offerPriceText by remember { mutableStateOf("") }
    var offerQuantityText by remember { mutableStateOf("") } // kg
    var offerNoteText by remember { mutableStateOf("") }
    var offerSubmitting by remember { mutableStateOf(false) }
    var offerSubmitError by remember { mutableStateOf<String?>(null) }
    var refreshTick by remember { mutableStateOf(0) }

    fun parseDoubleOrNull(s: String): Double? = s.trim().takeIf { it.isNotEmpty() }?.replace(',', '.')?.toDoubleOrNull()

    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.fetchOpenMeatSaleRequests()
        if (res.success) {
            listings = res.data ?: emptyList()
        } else {
            error = res.message ?: "Liste alınamadı"
        }
        isLoading = false
    }

    LaunchedEffect(refreshTick) { refresh() }

    val filteredListings = remember(listings, query, filters) {
        val q = query.trim().lowercase()
        val meatType = filters.meatType.trim().lowercase()
        val qMin = parseDoubleOrNull(filters.quantityMin)
        val qMax = parseDoubleOrNull(filters.quantityMax)

        fun matches(item: MeatSaleRequestDto): Boolean {
            if (q.isNotBlank()) {
                val inTitle = item.title.lowercase().contains(q)
                val inMeat = item.meatType.lowercase().contains(q)
                val inSlaughterhouse = (item.slaughterhouseName ?: "").lowercase().contains(q)
                if (!inTitle && !inMeat && !inSlaughterhouse) return false
            }
            if (meatType.isNotBlank() && !item.meatType.lowercase().contains(meatType)) return false
            val qty = item.quantity
            if (qMin != null && (qty == null || qty < qMin)) return false
            if (qMax != null && (qty == null || qty > qMax)) return false
            return true
        }

        val base = listings.filter(::matches)
        when (filters.sort) {
            "qtyasc" -> base.sortedBy { it.quantity ?: Double.MAX_VALUE }
            "qtydesc" -> base.sortedByDescending { it.quantity ?: Double.MIN_VALUE }
            else -> base // newest: backend order
        }
    }

    val activeFilterCount = remember(filters) {
        listOf(
            filters.sort != "newest",
            filters.meatType.isNotBlank(),
            filters.quantityMin.isNotBlank(),
            filters.quantityMax.isNotBlank(),
        ).count { it }
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
                        Text(text = "🥩 Et İlanları", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Kesimhanelerin açık ilanları", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Text(
                        text = "${filteredListings.size} ilan",
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
                        label = { Text("Başlık, et türü, kesimhane...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedButton(onClick = { filterOpen = true }) {
                        Text(if (activeFilterCount > 0) "Filtre ($activeFilterCount)" else "Filtre")
                    }
                }

                if (activeFilterCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        @Composable
                        fun Chip(text: String, onClear: () -> Unit) {
                            TextButton(
                                onClick = onClear,
                                modifier = Modifier.background(Color(0xFF1B3A6B).copy(alpha = 0.10f), androidx.compose.foundation.shape.RoundedCornerShape(999.dp)),
                            ) {
                                Text("$text  ✕", color = Color(0xFF1B3A6B), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        if (filters.sort != "newest") Chip(
                            text = when (filters.sort) {
                                "qtyasc" -> "Azdan çoğa"
                                "qtydesc" -> "Çoktan aza"
                                else -> "Sıralama"
                            }
                        ) { filters = filters.copy(sort = "newest") }

                        if (filters.meatType.isNotBlank()) Chip("Tür: ${filters.meatType}") { filters = filters.copy(meatType = "") }
                        if (filters.quantityMin.isNotBlank()) Chip("Kg≥${filters.quantityMin}") { filters = filters.copy(quantityMin = "") }
                        if (filters.quantityMax.isNotBlank()) Chip("Kg≤${filters.quantityMax}") { filters = filters.copy(quantityMax = "") }
                    }
                }
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            filteredListings.isEmpty() -> Text("Uygun ilan bulunamadı.", color = Color(0xFF64748B))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filteredListings) { item ->
                    FigmaCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = item.title, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Et türü: ${item.meatType} • Miktar: ${item.quantity ?: "-"} kg",
                                color = Color(0xFF64748B),
                            )
                            Text(
                                text = "Kesimhane: ${item.slaughterhouseName ?: (item.slaughterhouseId ?: "-")}",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                FigmaPrimaryButton(
                                    text = "Teklif ver",
                                    onClick = {
                                        offerDialogListing = item
                                        offerPriceText = ""
                                        offerQuantityText = item.quantity?.toString() ?: ""
                                        offerNoteText = ""
                                        offerSubmitError = null
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                                FigmaSecondaryButton(
                                    text = "Yenile",
                                    onClick = { refreshTick++ },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading,
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
                    FigmaSecondaryButton("Azdan", onClick = { filters = filters.copy(sort = "qtyasc") }, modifier = Modifier.weight(1f))
                    FigmaSecondaryButton("Çoktan", onClick = { filters = filters.copy(sort = "qtydesc") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(
                    value = filters.meatType,
                    onValueChange = { filters = filters.copy(meatType = it) },
                    label = { Text("Et türü (örn: dana)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = filters.quantityMin,
                        onValueChange = { filters = filters.copy(quantityMin = it) },
                        label = { Text("Kg min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = filters.quantityMax,
                        onValueChange = { filters = filters.copy(quantityMax = it) },
                        label = { Text("Kg max") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = "Sıfırla",
                        onClick = { filters = BuyerSearchFilters() },
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

    val dialogListing = offerDialogListing
    if (dialogListing != null) {
        AlertDialog(
            onDismissRequest = { if (!offerSubmitting) offerDialogListing = null },
            title = { Text("Teklif ver") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = dialogListing.title, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = offerPriceText,
                        onValueChange = { offerPriceText = it },
                        label = { Text("Fiyat (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = offerQuantityText,
                        onValueChange = { offerQuantityText = it },
                        label = { Text("Miktar (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = offerNoteText,
                        onValueChange = { offerNoteText = it },
                        label = { Text("Not (opsiyonel)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    offerSubmitError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !offerSubmitting,
                    onClick = { offerSubmitting = true }
                ) { Text("Gönder") }
            },
            dismissButton = {
                TextButton(
                    enabled = !offerSubmitting,
                    onClick = { offerDialogListing = null }
                ) { Text("Vazgeç") }
            }
        )

        LaunchedEffect(offerSubmitting) {
            if (!offerSubmitting) return@LaunchedEffect

            val price = offerPriceText.trim().replace(',', '.').toDoubleOrNull()
            val qty = offerQuantityText.trim().replace(',', '.').toDoubleOrNull()
            if (price == null || qty == null || qty <= 0) {
                offerSubmitError = "Fiyat ve miktar geçerli olmalı."
                offerSubmitting = false
                return@LaunchedEffect
            }

            val res = marketService.createBuyerMeatOffer(
                saleRequestId = dialogListing.id,
                payload = CreateMeatOfferPayload(
                    pricePerKg = price,
                    quantity = qty,
                    note = offerNoteText.trim().ifBlank { null },
                )
            )

            if (!res.success) {
                offerSubmitError = res.message ?: "Teklif gönderilemedi"
                offerSubmitting = false
                return@LaunchedEffect
            }

            offerSubmitting = false
            offerDialogListing = null
            refresh()
        }
    }
}

