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
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.platform.rememberShareTextAction
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DetailGridCell
import com.derdimet.mobil.ui.components.DerdimReviewsPlaceholder
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.InitialsAvatar
import com.derdimet.mobil.ui.components.OwnerCard
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber

@Composable
fun AnimalPurchaseRequestDetailScreen(
    requestId: Long,
    marketService: MarketService,
    onBack: () -> Unit,
    onMakeOffer: (AnimalPurchaseRequestDto) -> Unit,
    onMessage: (Long) -> Unit,
    onOpenSlaughterhouseProfile: (Long) -> Unit,
    initialRequest: AnimalPurchaseRequestDto? = null,
    isFavorited: Boolean? = null,
    favoriteError: String? = null,
    onFavoriteToggle: (() -> Unit)? = null,
) {
    var loading by remember(requestId) { mutableStateOf(initialRequest == null) }
    var error by remember(requestId) { mutableStateOf<String?>(null) }
    var req by remember(requestId) { mutableStateOf(initialRequest) }
    val shareText = rememberShareTextAction()

    LaunchedEffect(requestId) {
        if (initialRequest == null) {
            loading = true
            error = null
        }
        val res = marketService.fetchAnimalPurchaseRequestDetail(requestId)
        if (res.success && res.data != null) {
            req = res.data
            error = null
        } else if (req == null) {
            error = res.message ?: "Detay yüklenemedi"
        }
        loading = false
    }

    val r = req
    val favoriteTargetId = r?.slaughterhouseId
    val showFavorited = isFavorited ?: (r?.isFavoritedByMe == true)

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "İlan Detayı",
            showBack = true,
            onBack = onBack,
            action = {
                Row {
                    IconButton(onClick = {
                        shareText("derdimET alım talebi: ${r?.title ?: requestId}")
                    }) { Icon(Icons.Default.Share, null) }
                    IconButton(onClick = { onFavoriteToggle?.invoke() }, enabled = favoriteTargetId != null && onFavoriteToggle != null) {
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
            r != null -> Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                when (r.animalCategory) {
                                    AnimalCategory.KUCUKBAS -> "Küçükbaş"
                                    AnimalCategory.BUYUKBAS -> "Büyükbaş"
                                    null -> "Alım talebi"
                                },
                                modifier = Modifier.background(DerdimColors.Secondary, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp),
                                color = DerdimColors.Primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Text(r.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Row(Modifier.fillMaxWidth().background(DerdimColors.Primary.copy(0.08f), RoundedCornerShape(16.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Talep", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                                Text("${r.quantity ?: "-"} adet", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = DerdimColors.Primary)
                            }
                            Text("Açık", modifier = Modifier.background(DerdimColors.Green100, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), color = DerdimColors.Green700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailGridCell("Kategori", r.animalCategory?.name ?: "—", Modifier.weight(1f))
                            DetailGridCell("Adet", "${r.quantity ?: "-"}", Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailGridCell("Beklenen ağırlık", r.expectedWeight?.let { "${formatNumber(it)} kg" } ?: "—", Modifier.weight(1f))
                            DetailGridCell("Şehir", r.slaughterhouseCity ?: "—", Modifier.weight(1f))
                        }
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Text("Talep Açıklaması", fontWeight = FontWeight.SemiBold)
                            Text(r.description ?: "Açıklama bulunmuyor.", fontSize = 14.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 8.dp))
                        }
                        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                            Text("Kesimhane", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                            OwnerCard(name = r.slaughterhouseName, companyName = r.slaughterhouseCompanyName, city = r.slaughterhouseCity, onClick = { r.slaughterhouseId?.let(onOpenSlaughterhouseProfile) })
                        }
                        DerdimReviewsPlaceholder()
                        Row(Modifier.fillMaxWidth().background(DerdimColors.Amber50, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Amber100, RoundedCornerShape(16.dp)).padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.Shield, null, tint = DerdimColors.Amber600)
                            Column {
                                Text("Güvenli İşlem", fontWeight = FontWeight.SemiBold)
                                Text("Hayvan sağlık belgelerini ve tartı kayıtlarını teklif öncesi doğrulayın.", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
                Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(), shadowElevation = 12.dp, color = Color.White) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(r.slaughterhouseCompanyName ?: r.slaughterhouseName, size = 40)
                        Column(Modifier.weight(1f)) {
                            Text(r.slaughterhouseCompanyName ?: r.slaughterhouseName ?: "Kesimhane", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("4.7 · 86 değerlendirme", fontSize = 11.sp, color = DerdimColors.MutedForeground)
                        }
                        FigmaSecondaryButton("Mesaj", onClick = { r.slaughterhouseId?.let(onMessage) }, enabled = r.slaughterhouseId != null)
                        FigmaPrimaryButton("Teklif Ver", onClick = { onMakeOffer(r) })
                    }
                }
            }
        }
    }

}
