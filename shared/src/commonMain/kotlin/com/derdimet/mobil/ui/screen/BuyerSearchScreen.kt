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
import com.derdimet.mobil.model.CreateMeatOfferPayload
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.service.MarketService

@Composable
fun BuyerSearchScreen(
    marketService: MarketService,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<MeatSaleRequestDto>>(emptyList()) }

    var offerDialogListing by remember { mutableStateOf<MeatSaleRequestDto?>(null) }
    var offerPriceText by remember { mutableStateOf("") }
    var offerQuantityText by remember { mutableStateOf("") } // kg
    var offerNoteText by remember { mutableStateOf("") }
    var offerSubmitting by remember { mutableStateOf(false) }
    var offerSubmitError by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(Unit) { refresh() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Arama", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "İlanları filtrele ve sırala.",
            color = Color.Gray,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(onClick = { /* Faz 3: filtre sheet */ }, modifier = Modifier.weight(1f)) {
                Text("Filtrele")
            }
            OutlinedButton(onClick = { /* Faz 3: sıralama */ }, modifier = Modifier.weight(1f)) {
                Text("Sırala")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(listings) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Et türü: ${item.meatType} • Miktar: ${item.quantity ?: "-"}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Text(
                                text = "Kesimhane: ${item.slaughterhouseName ?: (item.slaughterhouseId ?: "-")}",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(onClick = {
                                offerDialogListing = item
                                offerPriceText = ""
                                offerQuantityText = item.quantity?.toString() ?: ""
                                offerNoteText = ""
                                offerSubmitError = null
                            }) {
                                Text("Teklif ver")
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

