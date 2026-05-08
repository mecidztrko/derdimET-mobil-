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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.CreateMeatSaleRequestPayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.ImageCarousel

@Composable
fun SlaughterhouseCreateMeatSaleRequestScreen(
    marketService: MarketService,
) {
    var title by remember { mutableStateOf("") }
    var meatType by remember { mutableStateOf("") }
    var animalCategory by remember { mutableStateOf<AnimalCategory?>(null) }
    var cut by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var pricePerKgText by remember { mutableStateOf("") }
    var packaging by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }
    val imageUrls = remember { mutableStateListOf<String>() }

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
                Text(text = "🥩 Et İlanı Oluştur", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Et alıcıları bu ilana teklif verecek.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Hayvan kategorisi", fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaSecondaryButton(
                        text = if (animalCategory == AnimalCategory.KUCUKBAS) "✓ Küçükbaş" else "Küçükbaş",
                        onClick = { animalCategory = AnimalCategory.KUCUKBAS },
                        modifier = Modifier.weight(1f),
                    )
                    FigmaSecondaryButton(
                        text = if (animalCategory == AnimalCategory.BUYUKBAS) "✓ Büyükbaş" else "Büyükbaş",
                        onClick = { animalCategory = AnimalCategory.BUYUKBAS },
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = meatType,
                        onValueChange = { meatType = it },
                        label = { Text("Et türü") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = cut,
                        onValueChange = { cut = it },
                        label = { Text("Et bölgesi (örn: but)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Miktar (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = pricePerKgText,
                        onValueChange = { pricePerKgText = it },
                        label = { Text("Kg fiyatı (₺)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
                OutlinedTextField(
                    value = packaging,
                    onValueChange = { packaging = it },
                    label = { Text("Paketleme (örn: vakumlu 1 kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Konum") },
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
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Görseller", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = imageUrlInput,
                        onValueChange = { imageUrlInput = it },
                        label = { Text("Görsel URL") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    FigmaSecondaryButton(
                        text = "Ekle",
                        onClick = {
                            val u = imageUrlInput.trim()
                            if (u.isNotBlank()) {
                                imageUrls.add(u)
                                imageUrlInput = ""
                            }
                        },
                    )
                }
                if (imageUrls.isNotEmpty()) {
                    ImageCarousel(imageUrls = imageUrls.toList())
                    FigmaSecondaryButton(
                        text = "Tüm görselleri kaldır",
                        onClick = { imageUrls.clear() },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        success?.let { Text(it, color = Color(0xFF166534)) }

        Spacer(Modifier.height(2.dp))

        FigmaPrimaryButton(
            text = if (submitting) "Gönderiliyor..." else "İlanı yayınla",
            enabled = !submitting,
            onClick = { submitting = true },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(20.dp))
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect
        error = null
        success = null

        val q = quantityText.trim().replace(',', '.').toDoubleOrNull()
        val ppk = pricePerKgText.trim().replace(',', '.').toDoubleOrNull()
        if (title.isBlank() || meatType.isBlank() || q == null || q <= 0) {
            error = "Başlık, et türü ve geçerli miktar zorunlu."
            submitting = false
            return@LaunchedEffect
        }

        val res = marketService.createSlaughterhouseMeatSaleRequest(
            payload = CreateMeatSaleRequestPayload(
                title = title.trim(),
                meatType = meatType.trim(),
                animalCategory = animalCategory,
                cut = cut.trim().ifBlank { null },
                quantity = q,
                pricePerKg = ppk,
                packaging = packaging.trim().ifBlank { null },
                location = location.trim().ifBlank { null },
                description = description.trim().ifBlank { null },
                imageUrls = imageUrls.toList().ifEmpty { null },
            )
        )

        if (!res.success) {
            error = res.message ?: "İlan oluşturulamadı"
            submitting = false
            return@LaunchedEffect
        }

        success = "İlan oluşturuldu."
        title = ""
        meatType = ""
        animalCategory = null
        cut = ""
        quantityText = ""
        pricePerKgText = ""
        packaging = ""
        location = ""
        description = ""
        imageUrlInput = ""
        imageUrls.clear()
        submitting = false
    }
}
