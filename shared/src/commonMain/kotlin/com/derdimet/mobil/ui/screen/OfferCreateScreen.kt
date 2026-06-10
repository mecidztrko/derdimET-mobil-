package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OfferCreateScreen(
    title: String,
    subtitle: String?,
    contextLine: String?,
    showQuantityAsInt: Boolean,
    quantityLabel: String,
    referencePricePerKg: Double? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    submit: suspend (pricePerKg: Double, quantity: Double, note: String?) -> Pair<Boolean, String?>,
) {
    var priceText by remember { mutableStateOf(referencePricePerKg?.let { formatNumber(it) } ?: "") }
    var qtyText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val parsedPrice = priceText.trim().replace(',', '.').toDoubleOrNull()
    val parsedQty = if (showQuantityAsInt) {
        qtyText.trim().toIntOrNull()?.toDouble()
    } else {
        qtyText.trim().replace(',', '.').toDoubleOrNull()
    }
    val estimatedTotal = if (parsedPrice != null && parsedQty != null && parsedPrice > 0 && parsedQty > 0) {
        parsedPrice * parsedQty
    } else {
        null
    }

    fun sendOffer() {
        if (submitting || success) return
        scope.launch {
            error = null
            val price = parsedPrice
            val qty = parsedQty
            if (price == null || price <= 0.0 || qty == null || qty <= 0.0) {
                error = "Fiyat ve miktar geçerli olmalı."
                return@launch
            }
            submitting = true
            try {
                val (ok, errMsg) = submit(price, qty, noteText.trim().ifBlank { null })
                if (!ok) {
                    error = errMsg ?: "Teklif gönderilemedi"
                    submitting = false
                    return@launch
                }
                success = true
                delay(700)
                onSuccess()
            } catch (e: Exception) {
                error = e.message ?: "Beklenmeyen bir hata oluştu"
                submitting = false
            }
        }
    }

    Box(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        Column(Modifier.fillMaxSize()) {
            DerdimTopBar(title = "Teklif Oluştur", showBack = true, onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OfferHeroCard(title = title, subtitle = subtitle, contextLine = contextLine)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, DerdimColors.Border.copy(0.45f), RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Teklif Detayları", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DerdimColors.Foreground)
                    Text(
                        "Satıcıya iletilecek fiyat ve miktarı girin",
                        fontSize = 12.sp,
                        color = DerdimColors.MutedForeground,
                    )
                    OfferFormField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = "Kg fiyatı (₺)",
                        leading = { Icon(Icons.Default.LocalOffer, null, tint = DerdimColors.Primary, modifier = Modifier.size(20.dp)) },
                    )
                    OfferFormField(
                        value = qtyText,
                        onValueChange = { qtyText = it },
                        label = quantityLabel,
                        leading = { Icon(Icons.Default.Scale, null, tint = DerdimColors.Primary, modifier = Modifier.size(20.dp)) },
                    )
                    OfferFormField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = "Not (opsiyonel)",
                        leading = { Icon(Icons.Default.EditNote, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(20.dp)) },
                        minLines = 3,
                    )
                    error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }
                }

                estimatedTotal?.let { total ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(DerdimColors.Primary.copy(0.08f), DerdimColors.Amber50),
                                ),
                            )
                            .border(1.dp, DerdimColors.Primary.copy(0.15f), RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Tahmini Toplam", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                            Text(
                                "${formatNumber(total)} ₺",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = DerdimColors.Primary,
                            )
                        }
                        Text(
                            "${formatNumber(parsedPrice!!)} ₺/kg × ${formatNumber(parsedQty!!)}",
                            fontSize = 11.sp,
                            color = DerdimColors.MutedForeground,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DerdimColors.Muted)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Outlined.Info, null, tint = DerdimColors.Primary, modifier = Modifier.size(18.dp))
                    Text(
                        "Teklifiniz satıcı onayına sunulur. Kabul edilene kadar fiyatınızı tekliflerim ekranından takip edebilirsiniz.",
                        fontSize = 12.sp,
                        color = DerdimColors.MutedForeground,
                        lineHeight = 17.sp,
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shadowElevation = 16.dp,
            color = Color.White,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FigmaSecondaryButton(
                    text = "Vazgeç",
                    onClick = onBack,
                    enabled = !submitting && !success,
                    modifier = Modifier.weight(1f),
                )
                FigmaPrimaryButton(
                    text = when {
                        success -> "Gönderildi"
                        submitting -> "Gönderiliyor..."
                        else -> "Teklifi Gönder"
                    },
                    enabled = !submitting && !success,
                    onClick = { sendOffer() },
                    modifier = Modifier.weight(1.2f),
                )
            }
        }

        if (success) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.35f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(horizontal = 32.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = DerdimColors.Success, modifier = Modifier.size(52.dp))
                    Text("Teklif Gönderildi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Satıcıya iletildi", fontSize = 13.sp, color = DerdimColors.MutedForeground)
                }
            }
        }
    }
}

@Composable
private fun OfferHeroCard(
    title: String,
    subtitle: String?,
    contextLine: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(DerdimColors.Primary, Color(0xFF2563EB))))
            .padding(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("İlan", fontSize = 11.sp, color = Color.White.copy(0.75f), fontWeight = FontWeight.SemiBold)
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White, lineHeight = 26.sp)
            val chips = listOfNotNull(subtitle, contextLine).filter { it.isNotBlank() }
            if (chips.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chips.forEach { chip ->
                        Text(
                            chip,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.White.copy(0.16f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            color = Color.White.copy(0.95f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leading: @Composable () -> Unit,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leading,
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DerdimColors.Primary.copy(0.5f),
            unfocusedBorderColor = DerdimColors.Border.copy(0.6f),
            focusedContainerColor = DerdimColors.Muted.copy(0.35f),
            unfocusedContainerColor = DerdimColors.Muted.copy(0.2f),
        ),
    )
}
