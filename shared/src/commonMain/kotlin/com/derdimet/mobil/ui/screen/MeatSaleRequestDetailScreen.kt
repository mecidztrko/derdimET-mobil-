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
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.util.formatNumber
import com.derdimet.mobil.ui.components.DetailRow
import com.derdimet.mobil.ui.components.DetailTopBar
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.ImageCarousel
import com.derdimet.mobil.ui.components.OwnerCard

/** Kesimhane tarafından oluşturulan et ilanı detayı (et alıcısı görüntüler). */
@Composable
fun MeatSaleRequestDetailScreen(
    saleRequestId: Long,
    marketService: MarketService,
    onBack: () -> Unit,
    onMakeOffer: (MeatSaleRequestDto) -> Unit,
    onMessage: (Long) -> Unit,
    onOpenSlaughterhouseProfile: (Long) -> Unit,
) {
    var loading by remember(saleRequestId) { mutableStateOf(true) }
    var error by remember(saleRequestId) { mutableStateOf<String?>(null) }
    var sale by remember(saleRequestId) { mutableStateOf<MeatSaleRequestDto?>(null) }
    var favSubmitting by remember { mutableStateOf(false) }

    suspend fun load() {
        loading = true
        error = null
        val res = marketService.fetchMeatSaleRequestDetail(saleRequestId)
        if (res.success) {
            sale = res.data
        } else {
            error = res.message ?: "Detay yüklenemedi"
        }
        loading = false
    }

    LaunchedEffect(saleRequestId) { load() }

    val s = sale
    Column(
        modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg),
    ) {
        DetailTopBar(
            title = s?.title ?: "Et ilanı",
            onBack = onBack,
            isFavorited = s?.isFavoritedByMe,
            onToggleFavorite = if (s?.slaughterhouseId != null) {
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
            s != null -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ImageCarousel(imageUrls = s.imageUrls)

                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = s.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = s.meatType,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        DetailRow("Hayvan kategorisi", s.animalCategory?.name)
                        DetailRow("Et bölgesi", s.cut)
                        DetailRow("Toplam miktar", s.quantity?.let { "${formatNumber(it)} kg" })
                        DetailRow("Kg fiyatı", s.pricePerKg?.let { "${formatNumber(it)} ₺" })
                        DetailRow("Paketleme", s.packaging)
                        DetailRow("Konum", s.location)
                        if (!s.description.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = s.description, color = Color(0xFF334155), fontSize = 13.sp)
                        }
                    }
                }

                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "İlan sahibi", fontWeight = FontWeight.SemiBold)
                        OwnerCard(
                            name = s.slaughterhouseName,
                            companyName = s.slaughterhouseCompanyName,
                            city = s.slaughterhouseCity,
                            onClick = { s.slaughterhouseId?.let(onOpenSlaughterhouseProfile) },
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
                        onClick = { s.slaughterhouseId?.let(onMessage) },
                        enabled = s.slaughterhouseId != null,
                        modifier = Modifier.weight(1f),
                    )
                    FigmaPrimaryButton(
                        text = "Teklif ver",
                        onClick = { onMakeOffer(s) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (favSubmitting) {
        LaunchedEffect(Unit) {
            val sid = sale?.slaughterhouseId
            if (sid != null) {
                val res = marketService.toggleFavorite(sid)
                if (res.success) {
                    sale = sale?.copy(isFavoritedByMe = res.data?.isFavoritedByMe == true)
                }
            }
            favSubmitting = false
        }
    }
}
