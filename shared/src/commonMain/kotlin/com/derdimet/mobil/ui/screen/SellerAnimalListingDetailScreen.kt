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
import com.derdimet.mobil.model.SellerAnimalListingDto
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

/** Hayvan satıcısı tarafından oluşturulan ilan detayı (kesimhane görüntüler). */
@Composable
fun SellerAnimalListingDetailScreen(
    listingId: Long,
    marketService: MarketService,
    onBack: () -> Unit,
    onMakeOffer: (SellerAnimalListingDto) -> Unit,
    onMessage: (Long) -> Unit,
    onOpenSellerProfile: (Long) -> Unit,
) {
    var loading by remember(listingId) { mutableStateOf(true) }
    var error by remember(listingId) { mutableStateOf<String?>(null) }
    var listing by remember(listingId) { mutableStateOf<SellerAnimalListingDto?>(null) }
    var favSubmitting by remember { mutableStateOf(false) }

    suspend fun load() {
        loading = true
        error = null
        val res = marketService.fetchAnimalListingDetail(listingId)
        if (res.success) {
            listing = res.data
        } else {
            error = res.message ?: "Detay yüklenemedi"
        }
        loading = false
    }

    LaunchedEffect(listingId) { load() }

    val l = listing
    Column(
        modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg),
    ) {
        DetailTopBar(
            title = l?.type ?: "İlan detayı",
            onBack = onBack,
            isFavorited = l?.isFavoritedByMe,
            onToggleFavorite = if (l?.sellerId != null) {
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
            l != null -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ImageCarousel(imageUrls = l.imageUrls)

                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = l.type, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = l.category.name,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        DetailRow("Irk", l.breed)
                        DetailRow("Yaş", l.ageMonths?.let { "$it ay" })
                        DetailRow("Adet", l.quantity.toString())
                        DetailRow("Ortalama ağırlık", l.avgWeightKg?.let { "${formatNumber(it)} kg" })
                        DetailRow("Fiyat", l.price?.let { "${formatNumber(it)} ₺" })
                        DetailRow("Konum", l.location)
                        if (!l.description.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(text = l.description, color = Color(0xFF334155), fontSize = 13.sp)
                        }
                    }
                }

                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "İlan sahibi", fontWeight = FontWeight.SemiBold)
                        OwnerCard(
                            name = l.sellerName,
                            companyName = l.sellerCompanyName,
                            city = l.sellerCity,
                            onClick = { l.sellerId?.let(onOpenSellerProfile) },
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
                        onClick = { l.sellerId?.let(onMessage) },
                        enabled = l.sellerId != null,
                        modifier = Modifier.weight(1f),
                    )
                    FigmaPrimaryButton(
                        text = "Teklif ver",
                        onClick = { onMakeOffer(l) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (favSubmitting) {
        LaunchedEffect(Unit) {
            val sid = listing?.sellerId
            if (sid != null) {
                val res = marketService.toggleFavorite(sid)
                if (res.success) {
                    listing = listing?.copy(isFavoritedByMe = res.data?.isFavoritedByMe == true)
                }
            }
            favSubmitting = false
        }
    }
}
