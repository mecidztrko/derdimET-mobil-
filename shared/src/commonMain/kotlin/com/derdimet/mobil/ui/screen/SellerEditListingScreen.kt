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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.model.UpdateSellerAnimalListingPayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimImageUploadSection
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SellerEditListingScreen(
    listing: SellerAnimalListingDto,
    marketService: MarketService,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    var category by remember(listing.id) { mutableStateOf(listing.category) }
    var type by remember(listing.id) { mutableStateOf(listing.type) }
    var breed by remember(listing.id) { mutableStateOf(listing.breed.orEmpty()) }
    var ageMonths by remember(listing.id) { mutableStateOf(listing.ageMonths?.toString().orEmpty()) }
    var quantity by remember(listing.id) { mutableStateOf(listing.quantity.toString()) }
    var avgWeightKg by remember(listing.id) { mutableStateOf(listing.avgWeightKg?.toString().orEmpty()) }
    var price by remember(listing.id) { mutableStateOf(listing.price?.toString().orEmpty()) }
    var location by remember(listing.id) { mutableStateOf(listing.location.orEmpty()) }
    var description by remember(listing.id) { mutableStateOf(listing.description.orEmpty()) }
    var imageUrlInput by remember(listing.id) { mutableStateOf("") }
    val imageUrls = remember(listing.id) { mutableStateListOf(*listing.imageUrls.toTypedArray()) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "İlanı Düzenle", showBack = true, onBack = onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DerdimFormCard(title = "İlan Bilgileri", subtitle = "Açık ilanlar düzenlenebilir") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = if (category == AnimalCategory.KUCUKBAS) "✓ Küçükbaş" else "Küçükbaş",
                        onClick = { category = AnimalCategory.KUCUKBAS },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaSecondaryButton(
                        text = if (category == AnimalCategory.BUYUKBAS) "✓ Büyükbaş" else "Büyükbaş",
                        onClick = { category = AnimalCategory.BUYUKBAS },
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Tür (örn: dana, kuzu)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Irk (örn: Simental)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ageMonths,
                        onValueChange = { ageMonths = it },
                        label = { Text("Yaş (ay)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Adet") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = avgWeightKg,
                        onValueChange = { avgWeightKg = it },
                        label = { Text("Ort. ağırlık (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Fiyat (₺)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Konum (il/ilçe)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama (opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
            }

            DerdimImageUploadSection(
                marketService = marketService,
                imageUrls = imageUrls.toList(),
                onImageUrlsChange = { imageUrls.clear(); imageUrls.addAll(it) },
                imageUrlInput = imageUrlInput,
                onImageUrlInputChange = { imageUrlInput = it },
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            success?.let { Text(it, color = Color(0xFF166534)) }

            Spacer(Modifier.height(2.dp))

            FigmaPrimaryButton(
                text = if (submitting) "Kaydediliyor..." else "Değişiklikleri kaydet",
                enabled = !submitting,
                onClick = { submitting = true },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(20.dp))
        }
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect
        error = null
        success = null

        val qty = quantity.trim().toIntOrNull()
        val age = ageMonths.trim().toIntOrNull()
        val avgW = avgWeightKg.trim().replace(',', '.').toDoubleOrNull()
        val p = price.trim().replace(',', '.').toDoubleOrNull()

        if (qty == null || qty <= 0 || type.isBlank()) {
            error = "Tür ve adet zorunlu."
            submitting = false
            return@LaunchedEffect
        }

        val res = marketService.updateSellerAnimalListing(
            listingId = listing.id,
            payload = UpdateSellerAnimalListingPayload(
                category = category,
                type = type.trim(),
                breed = breed.trim().ifBlank { null },
                ageMonths = age,
                quantity = qty,
                avgWeightKg = avgW,
                price = p,
                location = location.trim().ifBlank { null },
                description = description.trim().ifBlank { null },
                imageUrls = imageUrls.toList(),
            ),
        )

        if (!res.success) {
            error = res.message ?: "İlan güncellenemedi"
            submitting = false
            return@LaunchedEffect
        }

        success = "İlan güncellendi."
        submitting = false
        onSaved()
    }
}
