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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.CreateAnimalPurchasePayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun SlaughterhouseCreateAnimalPurchaseScreen(marketService: MarketService) {
    var step by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var animalCategory by remember { mutableStateOf<AnimalCategory?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var expectedWeightText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Adım ${step + 1}/2", fontSize = 12.sp, color = DerdimColors.MutedForeground, fontWeight = FontWeight.SemiBold)

        if (step == 0) {
            DerdimFormCard(title = "Talep bilgileri", subtitle = "Satıcılardan hayvan teklifi alın") {
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
                OutlinedTextField(title, { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(quantityText, { quantityText = it }, label = { Text("Adet") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(expectedWeightText, { expectedWeightText = it }, label = { Text("Beklenen kg") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                OutlinedTextField(description, { description = it }, label = { Text("Açıklama") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
            FigmaPrimaryButton("Önizleme", onClick = {
                val cat = animalCategory
                val qty = quantityText.trim().toIntOrNull()
                if (title.isBlank() || cat == null || qty == null || qty <= 0) {
                    error = "Başlık, kategori ve adet zorunlu."
                } else {
                    error = null
                    step = 1
                }
            }, modifier = Modifier.fillMaxWidth())
        } else {
            DerdimFormCard(title = "Önizleme", subtitle = title) {
                Text("Kategori: ${animalCategory?.name ?: "—"}", fontSize = 13.sp)
                Text("Adet: $quantityText", fontSize = 13.sp)
                expectedWeightText.toDoubleOrNull()?.let { Text("Beklenen ağırlık: $it kg", fontSize = 13.sp) }
                if (description.isNotBlank()) Text(description, fontSize = 13.sp, color = DerdimColors.MutedForeground)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FigmaSecondaryButton("Geri", onClick = { step = 0 }, modifier = Modifier.weight(1f))
                FigmaPrimaryButton(
                    text = if (submitting) "Gönderiliyor..." else "Yayınla",
                    enabled = !submitting,
                    onClick = { submitting = true },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        success?.let { Text(it, color = Color(0xFF166534)) }
        Spacer(Modifier.height(20.dp))
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect
        error = null
        success = null
        val cat = animalCategory ?: run { submitting = false; return@LaunchedEffect }
        val qty = quantityText.trim().toIntOrNull() ?: run { submitting = false; return@LaunchedEffect }
        val weight = expectedWeightText.trim().replace(',', '.').toDoubleOrNull()
        val res = marketService.createAnimalPurchaseRequest(
            CreateAnimalPurchasePayload(
                title = title.trim(),
                animalCategory = cat,
                quantity = qty,
                expectedWeight = weight,
                description = description.trim().ifBlank { null },
            )
        )
        if (res.success) {
            success = "Alım talebi oluşturuldu."
            title = ""
            animalCategory = null
            quantityText = ""
            expectedWeightText = ""
            description = ""
            step = 0
        } else {
            error = res.message ?: "Talep oluşturulamadı"
        }
        submitting = false
    }
}
