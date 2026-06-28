package com.derdimet.mobil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.OfferEventDto
import com.derdimet.mobil.model.OfferEventType
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferReviseSheet(
    title: String,
    quantityLabel: String,
    showQuantityAsInt: Boolean,
    initialPrice: Double?,
    initialQuantity: Double?,
    initialNote: String?,
    onDismiss: () -> Unit,
    onSubmit: suspend (pricePerKg: Double, quantity: Double, note: String?) -> Pair<Boolean, String?>,
) {
    var priceText by remember { mutableStateOf(initialPrice?.let { formatNumber(it) } ?: "") }
    var qtyText by remember {
        mutableStateOf(
            initialQuantity?.let { if (showQuantityAsInt) it.toInt().toString() else formatNumber(it) } ?: "",
        )
    }
    var noteText by remember { mutableStateOf(initialNote.orEmpty()) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Teklifi revize et", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(title, fontSize = 13.sp, color = DerdimColors.MutedForeground)
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Kg fiyatı (₺)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = qtyText,
                onValueChange = { qtyText = it },
                label = { Text(quantityLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Not (opsiyonel)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            error?.let { Text(it, color = DerdimColors.Red600, fontSize = 12.sp) }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FigmaSecondaryButton(
                    text = "Vazgeç",
                    onClick = onDismiss,
                    enabled = !submitting,
                    modifier = Modifier.weight(1f),
                )
                FigmaPrimaryButton(
                    text = if (submitting) "Kaydediliyor..." else "Revize et",
                    enabled = !submitting,
                    onClick = {
                        if (submitting) return@FigmaPrimaryButton
                        val price = priceText.trim().replace(',', '.').toDoubleOrNull()
                        val qty = if (showQuantityAsInt) {
                            qtyText.trim().toIntOrNull()?.toDouble()
                        } else {
                            qtyText.trim().replace(',', '.').toDoubleOrNull()
                        }
                        if (price == null || price <= 0 || qty == null || qty <= 0) {
                            error = "Fiyat ve miktar geçerli olmalı."
                            return@FigmaPrimaryButton
                        }
                        scope.launch {
                            submitting = true
                            error = null
                            val (ok, msg) = onSubmit(price, qty, noteText.trim().ifBlank { null })
                            submitting = false
                            if (ok) onDismiss() else error = msg ?: "Revize edilemedi"
                        }
                    },
                    modifier = Modifier.weight(1.2f),
                )
            }
            Text(
                "Revize sonrası teklif süresi 48 saat yenilenir.",
                fontSize = 11.sp,
                color = DerdimColors.MutedForeground,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferHistorySheet(
    events: List<OfferEventDto>,
    loading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Teklif geçmişi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            when {
                loading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground, fontSize = 13.sp)
                error != null -> Text(error, color = DerdimColors.Red600, fontSize = 13.sp)
                events.isEmpty() -> Text("Geçmiş kaydı yok.", color = DerdimColors.MutedForeground, fontSize = 13.sp)
                else -> events.forEach { event ->
                    Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text(
                            when (event.eventType) {
                                OfferEventType.CREATED -> "Oluşturuldu"
                                OfferEventType.REVISED -> "Revize #${event.revisionNumber ?: "?"}"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                        )
                        val price = event.pricePerKg?.let { "${formatNumber(it)} ₺/kg" }
                        val qty = event.quantity?.let { formatNumber(it) }
                        val detail = listOfNotNull(price, qty?.let { "Miktar: $it" }).joinToString(" · ")
                        if (detail.isNotBlank()) {
                            Text(detail, fontSize = 12.sp, color = DerdimColors.MutedForeground)
                        }
                        event.note?.takeIf { it.isNotBlank() }?.let {
                            Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground)
                        }
                        Text(event.createdAt.take(16).replace('T', ' '), fontSize = 11.sp, color = DerdimColors.MutedForeground)
                    }
                }
            }
            FigmaSecondaryButton(
                text = "Kapat",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            )
        }
    }
}
