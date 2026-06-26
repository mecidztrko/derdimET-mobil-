package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
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
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.platform.rememberShareTextAction
import com.derdimet.mobil.ui.components.DerdimUserReviewsSection
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.DetailGridCell
import com.derdimet.mobil.ui.components.ImageCarousel
import com.derdimet.mobil.ui.components.InitialsAvatar
import com.derdimet.mobil.ui.components.OwnerCard
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.ui.theme.DerdimTypeStyle
import com.derdimet.mobil.util.formatNumber
import com.derdimet.mobil.util.formatReviewSummary

@Composable
fun MeatSaleRequestDetailScreen(
    saleRequestId: Long,
    marketService: MarketService,
    onBack: () -> Unit,
    onMakeOffer: (MeatSaleRequestDto) -> Unit,
    onMessage: (Long) -> Unit,
    onOpenSlaughterhouseProfile: (Long) -> Unit,
    initialListing: MeatSaleRequestDto? = null,
    isFavorited: Boolean? = null,
    favoriteError: String? = null,
    onFavoriteToggle: (() -> Unit)? = null,
) {
    var loading by remember(saleRequestId) { mutableStateOf(initialListing == null) }
    var error by remember(saleRequestId) { mutableStateOf<String?>(null) }
    var sale by remember(saleRequestId) { mutableStateOf(initialListing) }
    var reviewSummaryText by remember(saleRequestId) { mutableStateOf<String?>(null) }
    val shareText = rememberShareTextAction()

    LaunchedEffect(saleRequestId) {
        if (initialListing == null) {
            loading = true
            error = null
        }
        val res = marketService.fetchMeatSaleRequestDetail(saleRequestId)
        if (res.success && res.data != null) {
            sale = res.data
            error = null
        } else if (sale == null) {
            error = res.message ?: "Detay yüklenemedi"
        }
        loading = false
    }

    val s = sale
    val favoriteTargetId = s?.slaughterhouseId
    val showFavorited = isFavorited ?: (s?.isFavoritedByMe == true)

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "İlan Detayı",
            showBack = true,
            onBack = onBack,
            action = {
                Row {
                    IconButton(onClick = {
                        shareText("derdimET ilanı: ${s?.title ?: saleRequestId} — ${s?.meatType ?: ""}")
                    }) { Icon(Icons.Default.Share, null) }
                    IconButton(
                        onClick = { onFavoriteToggle?.invoke() },
                        enabled = favoriteTargetId != null && onFavoriteToggle != null,
                    ) {
                        Icon(
                            if (showFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            null,
                            tint = if (showFavorited) Color(0xFFE05C2A) else DerdimColors.MutedForeground,
                        )
                    }
                }
            },
        )
        favoriteError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Yükleniyor...", color = DerdimColors.MutedForeground) }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error ?: "Hata", color = MaterialTheme.colorScheme.error) }
            s != null -> Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {
                    ImageCarousel(imageUrls = s.imageUrls)
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(s.meatType, modifier = Modifier.background(DerdimColors.Secondary, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), color = DerdimColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DerdimColors.Foreground)
                        Row(Modifier.fillMaxWidth().background(DerdimColors.Primary.copy(0.08f), RoundedCornerShape(16.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Satış Fiyatı", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                                Text("${s.pricePerKg?.let { formatNumber(it) } ?: "-"} ₺ / kg", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = DerdimColors.Primary)
                            }
                            Text("Onaylı", modifier = Modifier.background(DerdimColors.Green100, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), color = DerdimColors.Green700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailGridCell("Tür", s.meatType, Modifier.weight(1f))
                            DetailGridCell("Miktar", "${s.quantity?.let { formatNumber(it) } ?: "-"} kg", Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailGridCell("Ortalama Ağırlık", "${s.quantity?.let { formatNumber(it) } ?: "-"} kg", Modifier.weight(1f))
                            DetailGridCell("Konum", s.location ?: s.slaughterhouseCity ?: "—", Modifier.weight(1f))
                        }
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Text("İlan Açıklaması", fontWeight = FontWeight.SemiBold)
                            Text(s.description ?: "Açıklama bulunmuyor.", fontSize = 14.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 8.dp))
                        }
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Text("Satıcı", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                            OwnerCard(name = s.slaughterhouseName, companyName = s.slaughterhouseCompanyName, city = s.slaughterhouseCity, onClick = { s.slaughterhouseId?.let(onOpenSlaughterhouseProfile) })
                        }
                        DerdimUserReviewsSection(
                            userId = s.slaughterhouseId,
                            marketService = marketService,
                            onSummaryLoaded = { avg, cnt -> reviewSummaryText = formatReviewSummary(avg, cnt) },
                        )
                        Row(Modifier.fillMaxWidth().background(DerdimColors.Amber50, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Amber100, RoundedCornerShape(16.dp)).padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Shield, null, tint = DerdimColors.Amber600)
                            Column {
                                Text("Güvenli Alışveriş", fontWeight = FontWeight.SemiBold)
                                Text("Ürünü görmeden ödeme yapmayın. Sağlık sertifikalarını kontrol edin.", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
                Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(), shadowElevation = 12.dp, color = Color.White) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(s.slaughterhouseCompanyName ?: s.slaughterhouseName, size = 40)
                        Column(Modifier.weight(1f)) {
                            Text(s.slaughterhouseCompanyName ?: s.slaughterhouseName ?: "Satıcı", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(s.slaughterhouseCity ?: "Kesimhane", fontSize = 11.sp, color = DerdimColors.MutedForeground)
                        }
                        FigmaSecondaryButton("Mesaj", onClick = { s.slaughterhouseId?.let(onMessage) }, enabled = s.slaughterhouseId != null)
                        FigmaPrimaryButton("Teklif Ver", onClick = { onMakeOffer(s) })
                    }
                }
            }
        }
    }

}

