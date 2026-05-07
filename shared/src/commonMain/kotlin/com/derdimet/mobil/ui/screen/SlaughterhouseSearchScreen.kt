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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.CreateSlaughterhouseListingOfferPayload
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.FavoriteSellerDto
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.service.MarketService

@Composable
fun SlaughterhouseSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<SellerAnimalListingDto>>(emptyList()) }
    var favoriteSellers by remember { mutableStateOf<List<FavoriteSellerDto>>(emptyList()) }
    var favSubmittingSellerId by remember { mutableStateOf<Long?>(null) }

    var category by remember { mutableStateOf<AnimalCategory?>(null) }
    var typeQuery by remember { mutableStateOf("") }
    var ageMinText by remember { mutableStateOf("") }
    var ageMaxText by remember { mutableStateOf("") }
    var quantityMinText by remember { mutableStateOf("") }
    var quantityMaxText by remember { mutableStateOf("") }
    var priceMinText by remember { mutableStateOf("") }
    var priceMaxText by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("newest") }

    var offerDialogListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }
    var offerPriceText by remember { mutableStateOf("") }
    var offerQuantityText by remember { mutableStateOf("") }
    var offerNoteText by remember { mutableStateOf("") }
    var offerSubmitting by remember { mutableStateOf(false) }
    var offerSubmitError by remember { mutableStateOf<String?>(null) }

    fun parseIntOrNull(s: String): Int? = s.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    fun parseDoubleOrNull(s: String): Double? = s.trim().takeIf { it.isNotEmpty() }?.replace(',', '.')?.toDoubleOrNull()

    suspend fun refresh() {
        isLoading = true
        error = null

        val res = marketService.searchSlaughterhouseAnimalListingsFiltered(
            category = category?.name,
            type = typeQuery.takeIf { it.isNotBlank() },
            ageMin = parseIntOrNull(ageMinText),
            ageMax = parseIntOrNull(ageMaxText),
            quantityMin = parseIntOrNull(quantityMinText),
            quantityMax = parseIntOrNull(quantityMaxText),
            priceMin = parseDoubleOrNull(priceMinText),
            priceMax = parseDoubleOrNull(priceMaxText),
            sort = sort,
        )

        if (res.success) listings = res.data ?: emptyList() else error = res.message ?: "İlanlar alınamadı"
        isLoading = false
    }

    suspend fun refreshFavorites() {
        val fav = marketService.fetchSlaughterhouseFavoriteSellers()
        if (fav.success) {
            favoriteSellers = fav.data ?: emptyList()
        } else if (error == null) {
            error = fav.message ?: "Favoriler alınamadı"
        }
    }

    LaunchedEffect(
        category,
        typeQuery,
        ageMinText,
        ageMaxText,
        quantityMinText,
        quantityMaxText,
        priceMinText,
        priceMaxText,
        sort,
    ) { refresh() }

    LaunchedEffect(Unit) {
        refreshFavorites()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Arama", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Satıcıların açtığı açık ilanları filtreleyin.", color = Color.Gray)

        Text(text = "Kategori", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = { category = null }, modifier = Modifier.weight(1f)) { Text("Tümü") }
            OutlinedButton(onClick = { category = AnimalCategory.KUCUKBAS }, modifier = Modifier.weight(1f)) { Text("Küçükbaş") }
            OutlinedButton(onClick = { category = AnimalCategory.BUYUKBAS }, modifier = Modifier.weight(1f)) { Text("Büyükbaş") }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = typeQuery,
            onValueChange = { typeQuery = it },
            label = { Text("Tür (ör: Merinos)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = ageMinText,
                onValueChange = { ageMinText = it },
                label = { Text("Yaş min (ay)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = ageMaxText,
                onValueChange = { ageMaxText = it },
                label = { Text("Yaş max (ay)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = quantityMinText,
                onValueChange = { quantityMinText = it },
                label = { Text("Adet min") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = quantityMaxText,
                onValueChange = { quantityMaxText = it },
                label = { Text("Adet max") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = priceMinText,
                onValueChange = { priceMinText = it },
                label = { Text("Fiyat min") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                value = priceMaxText,
                onValueChange = { priceMaxText = it },
                label = { Text("Fiyat max") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }

        Text(text = "Sıralama", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = { sort = "newest" }, modifier = Modifier.weight(1f)) { Text("En yeni") }
            OutlinedButton(onClick = { sort = "priceasc" }, modifier = Modifier.weight(1f)) { Text("Ucuzdan") }
            OutlinedButton(onClick = { sort = "pricedesc" }, modifier = Modifier.weight(1f)) { Text("Pahalıdan") }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(listings) { l ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = l.type, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Kategori: ${l.category} • Yaş: ${l.ageMonths ?: "-"} • Adet: ${l.quantity}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = "Fiyat: ${l.price ?: "-"} • Durum: ${l.status}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = "Satıcı: ${l.sellerName ?: (l.sellerId ?: "-")}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            val sid = l.sellerId
                            val isFav = sid != null && favoriteSellers.any { it.sellerId == sid }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        offerDialogListing = l
                                        offerPriceText = l.price?.toString() ?: ""
                                        offerQuantityText = l.quantity.toString()
                                        offerNoteText = ""
                                        offerSubmitError = null
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Teklif ver")
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
    }

    val dialogListing = offerDialogListing
    if (dialogListing != null) {
        AlertDialog(
            onDismissRequest = { if (!offerSubmitting) offerDialogListing = null },
            title = { Text("Teklif ver") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = dialogListing.type, fontWeight = FontWeight.SemiBold)
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
                        label = { Text("Adet") },
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
                TextButton(enabled = !offerSubmitting, onClick = { offerSubmitting = true }) { Text("Gönder") }
            },
            dismissButton = {
                TextButton(enabled = !offerSubmitting, onClick = { offerDialogListing = null }) { Text("Vazgeç") }
            },
        )

        LaunchedEffect(offerSubmitting) {
            if (!offerSubmitting) return@LaunchedEffect

            offerSubmitError = null
            val price = parseDoubleOrNull(offerPriceText)
            val qty = offerQuantityText.trim().replace(',', '.').toIntOrNull()

            if (price == null || qty == null || qty <= 0) {
                offerSubmitError = "Fiyat ve adet geçerli olmalı."
                offerSubmitting = false
                return@LaunchedEffect
            }

            val res = marketService.createSlaughterhouseListingOffer(
                listingId = dialogListing.id,
                payload = CreateSlaughterhouseListingOfferPayload(
                    pricePerKg = price,
                    quantity = qty,
                    note = offerNoteText.trim().ifBlank { null },
                ),
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
        refreshFavorites()
    }
}

