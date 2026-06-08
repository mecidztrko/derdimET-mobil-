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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun OfferCreateScreen(
    title: String,
    subtitle: String?,
    contextLine: String?,
    showQuantityAsInt: Boolean,
    quantityLabel: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    submit: suspend (pricePerKg: Double, quantity: Double, note: String?) -> Pair<Boolean, String?>,
) {
    var priceText by remember { mutableStateOf("") }
    var qtyText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Teklif Ver", showBack = true, onBack = onBack)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DerdimFormCard(title = title, subtitle = listOfNotNull(subtitle, contextLine).joinToString(" · ").ifBlank { null }) {}
            Column {
                DerdimFormCard(title = "Teklif Bilgileri", subtitle = "Fiyat ve miktar girin") {
                    OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Kg fiyatı (₺)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = qtyText, onValueChange = { qtyText = it }, label = { Text(quantityLabel) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = noteText, onValueChange = { noteText = it }, label = { Text("Not (opsiyonel)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FigmaSecondaryButton(text = "Vazgeç", onClick = onBack, enabled = !submitting, modifier = Modifier.weight(1f))
                FigmaPrimaryButton(text = if (submitting) "Gönderiliyor..." else "Teklifi Gönder", enabled = !submitting, onClick = { submitting = true }, modifier = Modifier.weight(1f))
            }
        }
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect
        error = null
        val price = priceText.trim().replace(',', '.').toDoubleOrNull()
        val qty = if (showQuantityAsInt) qtyText.trim().toIntOrNull()?.toDouble()
        else qtyText.trim().replace(',', '.').toDoubleOrNull()
        if (price == null || price <= 0.0 || qty == null || qty <= 0.0) {
            error = "Fiyat ve adet/miktar geçerli olmalı."
            submitting = false
            return@LaunchedEffect
        }
        val (ok, errMsg) = submit(price, qty, noteText.trim().ifBlank { null })
        if (!ok) {
            error = errMsg ?: "Teklif gönderilemedi"
            submitting = false
            return@LaunchedEffect
        }
        submitting = false
        onSuccess()
    }
}
