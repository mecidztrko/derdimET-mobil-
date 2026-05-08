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
import com.derdimet.mobil.model.CreateMeatSaleRequestPayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun SlaughterhouseCreateMeatSaleRequestScreen(
    marketService: MarketService,
) {
    var title by remember { mutableStateOf("") }
    var meatType by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") } // kg
    var description by remember { mutableStateOf("") }

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
                Text(
                    text = "İpucu: Başlık net olursa daha hızlı teklif gelir (örn: “Dana kuşbaşı - günlük kesim”).",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                )
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "İlan detayları", fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = meatType,
                    onValueChange = { meatType = it },
                    label = { Text("Et türü (örn: dana, kuzu)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Miktar (kg)") },
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

                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                success?.let { Text(it, color = Color(0xFF166534)) }

                Spacer(modifier = Modifier.height(2.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FigmaPrimaryButton(
                        text = if (submitting) "Gönderiliyor..." else "İlanı yayınla",
                        enabled = !submitting,
                        onClick = { submitting = true },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect

        error = null
        success = null

        val q = quantityText.trim().replace(',', '.').toDoubleOrNull()
        if (title.isBlank() || meatType.isBlank() || q == null || q <= 0) {
            error = "Başlık, et türü ve geçerli miktar zorunlu."
            submitting = false
            return@LaunchedEffect
        }

        val res = marketService.createSlaughterhouseMeatSaleRequest(
            payload = CreateMeatSaleRequestPayload(
                title = title.trim(),
                meatType = meatType.trim(),
                quantity = q,
                description = description.trim().ifBlank { null },
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
        quantityText = ""
        description = ""
        submitting = false
    }
}

