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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.CreateSellerAnimalListingPayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SellerCreateListingScreen(
    marketService: MarketService,
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var ageMonths by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

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
                Text(text = "🐄 Hayvan İlanı Oluştur", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Kesimhaneler bu ilanı görüp teklif verebilir.", color = FigmaStyle.MutedText, fontSize = 12.sp)
                Text(
                    text = "İpucu: Tür/ırk ve adet doğru olursa daha hızlı dönüş alırsın.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                )
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "İlan detayları", fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (KUCUKBAS/BUYUKBAS)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Tür / Irk") },
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
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Fiyat (opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Görsel URL (şimdilik)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Açıklama (opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )

                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                success?.let { Text(it, color = Color(0xFF166534)) }

                Spacer(modifier = Modifier.height(2.dp))

                FigmaPrimaryButton(
                    text = if (submitting) "Gönderiliyor..." else "İlanı yayınla",
                    enabled = !submitting,
                    onClick = { submitting = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Not: Görsel yükleme şimdilik URL ile.",
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect
        error = null
        success = null

        val cat = runCatching { AnimalCategory.valueOf(category.trim().uppercase()) }.getOrNull()
        val qty = quantity.trim().toIntOrNull()
        val age = ageMonths.trim().toIntOrNull()
        val p = price.trim().replace(',', '.').toDoubleOrNull()

        if (cat == null || qty == null || qty <= 0 || type.isBlank()) {
            error = "Kategori, tür ve adet zorunlu."
            submitting = false
            return@LaunchedEffect
        }

        val res = marketService.createSellerAnimalListing(
            CreateSellerAnimalListingPayload(
                category = cat,
                type = type.trim(),
                ageMonths = age,
                quantity = qty,
                price = p,
                description = note.trim().ifBlank { null },
                imageUrls = imageUrl.trim().ifBlank { null }?.let { listOf(it) },
            )
        )

        if (!res.success) {
            error = res.message ?: "İlan oluşturulamadı"
            submitting = false
            return@LaunchedEffect
        }

        success = "İlan oluşturuldu."
        category = ""
        type = ""
        ageMonths = ""
        quantity = ""
        price = ""
        imageUrl = ""
        note = ""
        submitting = false
    }
}

