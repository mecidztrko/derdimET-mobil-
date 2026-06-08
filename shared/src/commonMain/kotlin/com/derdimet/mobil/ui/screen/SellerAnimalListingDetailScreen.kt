package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.platform.rememberShareTextAction
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DetailGridCell
import com.derdimet.mobil.ui.components.DerdimReviewsPlaceholder
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.ImageCarousel
import com.derdimet.mobil.ui.components.InitialsAvatar
import com.derdimet.mobil.ui.components.OwnerCard
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber

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
    val shareText = rememberShareTextAction()

    LaunchedEffect(listingId) {
        loading = true
        error = null
        val res = marketService.fetchAnimalListingDetail(listingId)
        if (res.success) listing = res.data else error = res.message ?: "Detay yüklenemedi"
        loading = false
    }

    val l = listing
    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "İlan Detayı",
            showBack = true,
            onBack = onBack,
            action = {
                Row {
                    IconButton(onClick = {
                        shareText("derdimET hayvan ilanı: ${l?.type ?: listingId} — ${l?.quantity ?: ""} adet")
                    }) { Icon(Icons.Default.Share, null) }
                    IconButton(onClick = { if (l?.sellerId != null) favSubmitting = true }) {
                        Icon(
                            if (l?.isFavoritedByMe == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            tint = if (l?.isFavoritedByMe == true) Color(0xFFE05C2A) else DerdimColors.MutedForeground,
                        )
                    }
                }
            },
        )
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Yükleniyor...", color = DerdimColors.MutedForeground) }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error ?: "Hata", color = MaterialTheme.colorScheme.error) }
            l != null -> Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {
                    ImageCarousel(imageUrls = l.imageUrls)
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                when (l.category) {
                                    AnimalCategory.KUCUKBAS -> "Küçükbaş"
                                    AnimalCategory.BUYUKBAS -> "Büyükbaş"
                                },
                                modifier = Modifier.background(DerdimColors.Secondary, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp),
                                color = DerdimColors.Primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            l.breed?.let {
                                Text(it, modifier = Modifier.background(DerdimColors.Muted, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp)
                            }
                        }
                        Text(l.type, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Row(Modifier.fillMaxWidth().background(DerdimColors.Primary.copy(0.08f), RoundedCornerShape(16.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Fiyat", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                                Text("${l.price?.let { formatNumber(it) } ?: "-"} ₺", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = DerdimColors.Primary)
                            }
                            Text("${l.quantity} adet", modifier = Modifier.background(DerdimColors.Green100, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), color = DerdimColors.Green700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailGridCell("Irk", l.breed ?: "—", Modifier.weight(1f))
                            DetailGridCell("Yaş", l.ageMonths?.let { "$it ay" } ?: "—", Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailGridCell("Ort. ağırlık", l.avgWeightKg?.let { "${formatNumber(it)} kg" } ?: "—", Modifier.weight(1f))
                            DetailGridCell("Konum", l.location ?: l.sellerCity ?: "—", Modifier.weight(1f))
                        }
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Text("İlan Açıklaması", fontWeight = FontWeight.SemiBold)
                            Text(l.description ?: "Açıklama bulunmuyor.", fontSize = 14.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 8.dp))
                        }
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Text("Satıcı", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                            OwnerCard(name = l.sellerName, companyName = l.sellerCompanyName, city = l.sellerCity, onClick = { l.sellerId?.let(onOpenSellerProfile) })
                        }
                        DerdimReviewsPlaceholder()
                        Row(Modifier.fillMaxWidth().background(DerdimColors.Amber50, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Amber100, RoundedCornerShape(16.dp)).padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Shield, null, tint = DerdimColors.Amber600)
                            Column {
                                Text("Güvenli Alışveriş", fontWeight = FontWeight.SemiBold)
                                Text("Hayvanı görmeden ödeme yapmayın. Sağlık belgelerini kontrol edin.", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
                Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(), shadowElevation = 12.dp, color = Color.White) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(l.sellerCompanyName ?: l.sellerName, size = 40)
                        Column(Modifier.weight(1f)) {
                            Text(l.sellerCompanyName ?: l.sellerName ?: "Satıcı", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("4.6 · 52 değerlendirme", fontSize = 11.sp, color = DerdimColors.MutedForeground)
                        }
                        FigmaSecondaryButton("Mesaj", onClick = { l.sellerId?.let(onMessage) }, enabled = l.sellerId != null)
                        FigmaPrimaryButton("Teklif Ver", onClick = { onMakeOffer(l) })
                    }
                }
            }
        }
    }

    if (favSubmitting) {
        LaunchedEffect(Unit) {
            val sid = listing?.sellerId
            if (sid != null) {
                val res = marketService.toggleFavorite(sid)
                if (res.success) listing = listing?.copy(isFavoritedByMe = res.data?.isFavoritedByMe == true)
            }
            favSubmitting = false
        }
    }
}
