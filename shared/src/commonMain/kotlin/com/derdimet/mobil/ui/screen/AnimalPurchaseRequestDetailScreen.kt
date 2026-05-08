package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.util.formatNumber
import com.derdimet.mobil.ui.components.DetailRow
import com.derdimet.mobil.ui.components.DetailTopBar
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.OwnerCard

/** Kesimhanenin açtığı hayvan alış ilanı detayı (hayvan satıcısı görüntüler). */
@Composable
fun AnimalPurchaseRequestDetailScreen(
    requestId: Long,
    marketService: MarketService,
    onBack: () -> Unit,
    onMakeOffer: (AnimalPurchaseRequestDto) -> Unit,
    onMessage: (Long) -> Unit,
    onOpenSlaughterhouseProfile: (Long) -> Unit,
) {
    var loading by remember(requestId) { mutableStateOf(true) }
    var error by remember(requestId) { mutableStateOf<String?>(null) }
    var req by remember(requestId) { mutableStateOf<AnimalPurchaseRequestDto?>(null) }
    var favSubmitting by remember { mutableStateOf(false) }

    suspend fun load() {
        loading = true
        error = null
        val res = marketService.fetchAnimalPurchaseRequestDetail(requestId)
        if (res.success) {
            req = res.data
        } else {
            error = res.message ?: "Detay yüklenemedi"
        }
        loading = false
    }

    LaunchedEffect(requestId) { load() }

    val r = req
    Column(
        modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg),
    ) {
        DetailTopBar(
            title = r?.title ?: "Alım ilanı",
            onBack = onBack,
            isFavorited = r?.isFavoritedByMe,
            onToggleFavorite = if (r?.slaughterhouseId != null) {
                { favSubmitting = true }
            } else null,
        )

        when {
            loading -> Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
            ) { Text(text = "Yükleniyor...", color = Color.Gray) }
            error != null -> Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
            ) { Text(text = error ?: "Hata", color = MaterialTheme.colorScheme.error) }
            r != null -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = r.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = r.animalCategory?.name ?: "Genel",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        DetailRow("Adet", r.quantity?.toString())
                        DetailRow("Beklenen ağırlık", r.expectedWeight?.let { "${formatNumber(it)} kg" })
                        if (!r.description.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = r.description, color = Color(0xFF334155), fontSize = 13.sp)
                        }
                    }
                }

                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Talep eden kesimhane", fontWeight = FontWeight.SemiBold)
                        OwnerCard(
                            name = r.slaughterhouseName,
                            companyName = r.slaughterhouseCompanyName,
                            city = r.slaughterhouseCity,
                            onClick = { r.slaughterhouseId?.let(onOpenSlaughterhouseProfile) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FigmaSecondaryButton(
                        text = "Mesaj at",
                        onClick = { r.slaughterhouseId?.let(onMessage) },
                        enabled = r.slaughterhouseId != null,
                        modifier = Modifier.weight(1f),
                    )
                    FigmaPrimaryButton(
                        text = "Teklif ver",
                        onClick = { onMakeOffer(r) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (favSubmitting) {
        LaunchedEffect(Unit) {
            val sid = req?.slaughterhouseId
            if (sid != null) {
                val res = marketService.toggleFavorite(sid)
                if (res.success) {
                    req = req?.copy(isFavoritedByMe = res.data?.isFavoritedByMe == true)
                }
            }
            favSubmitting = false
        }
    }
}
