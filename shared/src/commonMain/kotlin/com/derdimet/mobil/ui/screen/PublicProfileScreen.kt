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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.PublicUserListingsDto
import com.derdimet.mobil.model.PublicUserProfileDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber

@Composable
fun PublicProfileScreen(
    userId: Long,
    marketService: MarketService,
    onBack: () -> Unit,
    onMessage: (Long) -> Unit,
) {
    var loading by remember(userId) { mutableStateOf(true) }
    var error by remember(userId) { mutableStateOf<String?>(null) }
    var profile by remember(userId) { mutableStateOf<PublicUserProfileDto?>(null) }
    var listings by remember(userId) { mutableStateOf<PublicUserListingsDto?>(null) }
    var refreshKey by remember(userId) { mutableIntStateOf(0) }

    LaunchedEffect(userId, refreshKey) {
        loading = true
        error = null
        val res = marketService.fetchPublicProfile(userId)
        if (res.success) {
            profile = res.data
            val listRes = marketService.fetchPublicUserListings(userId)
            if (listRes.success) listings = listRes.data
        } else {
            error = res.message ?: "Profil yüklenemedi"
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Profil", showBack = true, onBack = onBack)
        DerdimScreenState(
            modifier = Modifier.fillMaxSize(),
            loading = loading,
            error = if (profile == null) error else null,
            empty = false,
            onRetry = { refreshKey++ },
        ) {
            val p = profile ?: return@DerdimScreenState
            val openListings = listings
            Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp))) {
                    Box(Modifier.fillMaxWidth().height(72.dp).background(Brush.horizontalGradient(listOf(DerdimColors.Primary, Color(0xFF3B82F6)))))
                    Column(Modifier.padding(16.dp)) {
                        Box(Modifier.size(64.dp).background(Color.White, RoundedCornerShape(16.dp)).border(4.dp, Color.White, RoundedCornerShape(16.dp)).padding(4.dp)) {
                            Box(Modifier.fillMaxSize().background(DerdimColors.Primary.copy(0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Text((p.companyName ?: p.name ?: "?").take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = DerdimColors.Primary)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(p.companyName ?: p.name ?: "İsim yok", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            if (p.businessVerified) Icon(Icons.Default.CheckCircle, null, tint = DerdimColors.Success, modifier = Modifier.padding(start = 6.dp).size(18.dp))
                        }
                        Text(roleLabel(p.role) ?: "", modifier = Modifier.padding(top = 6.dp).background(DerdimColors.Secondary, RoundedCornerShape(999.dp)).padding(horizontal = 10.dp, vertical = 4.dp), color = DerdimColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        p.city?.let { Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp)); Text(it, fontSize = 13.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 6.dp)) } }
                        p.companyName?.let { Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Business, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp)); Text(it, fontSize = 13.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 6.dp)) } }
                    }
                }
                DerdimFormCard(title = "İletişim Bilgileri") {
                    ProfileInfoRow("Yetkili", p.name)
                    ProfileInfoRow("E-posta doğrulama", if (p.emailVerified) "Doğrulandı" else "Bekliyor")
                    ProfileInfoRow("Şirket doğrulama", if (p.businessVerified) "Doğrulandı" else "Bekliyor")
                    ProfileInfoRow("Hesap tipi", p.accountType)
                    ProfileInfoRow("Adres", p.addressLine)
                }
                openListings?.let { data ->
                    val meat = data.meatListings
                    val animal = data.animalListings
                    if (meat.isNotEmpty() || animal.isNotEmpty()) {
                        DerdimFormCard(title = "Açık İlanlar", subtitle = "${meat.size + animal.size} ilan") {
                            meat.forEach { item ->
                                PublicListingRow(
                                    title = item.title,
                                    subtitle = item.meatType,
                                    detail = buildString {
                                        item.quantity?.let { append("${formatNumber(it)} kg") }
                                        item.pricePerKg?.let {
                                            if (isNotEmpty()) append(" · ")
                                            append("${formatNumber(it)} ₺/kg")
                                        }
                                    },
                                )
                            }
                            animal.forEach { item ->
                                PublicListingRow(
                                    title = item.type ?: "Hayvan ilanı",
                                    subtitle = item.breed,
                                    detail = buildString {
                                        append("${item.quantity} baş")
                                        item.price?.let { append(" · ${formatNumber(it)} ₺") }
                                    },
                                )
                            }
                        }
                    }
                }
                FigmaPrimaryButton("Mesaj At", onClick = { onMessage(p.id) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun PublicListingRow(title: String, subtitle: String?, detail: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(DerdimColors.Muted.copy(0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        subtitle?.takeIf { it.isNotBlank() }?.let {
            Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 2.dp))
        }
        if (detail.isNotBlank()) {
            Text(detail, fontSize = 12.sp, color = DerdimColors.Primary, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = DerdimColors.MutedForeground)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

private fun roleLabel(role: String?): String? = when (role) {
    "SLAUGHTERHOUSE" -> "Kesimhane"
    "ANIMAL_SELLER" -> "Hayvan Satıcısı"
    "MEAT_BUYER" -> "Et Alıcısı"
    "ADMIN" -> "Yönetici"
    else -> null
}
