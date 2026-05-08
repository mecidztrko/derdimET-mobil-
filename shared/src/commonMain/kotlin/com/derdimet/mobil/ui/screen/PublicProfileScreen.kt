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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.PublicUserProfileDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DetailRow
import com.derdimet.mobil.ui.components.DetailTopBar
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

/** Liste / detay sayfasından satıcı/kesimhane/alıcı ismine tıklayınca açılan public profil. */
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

    LaunchedEffect(userId) {
        loading = true
        error = null
        val res = marketService.fetchPublicProfile(userId)
        if (res.success) {
            profile = res.data
        } else {
            error = res.message ?: "Profil yüklenemedi"
        }
        loading = false
    }

    val p = profile
    Column(
        modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg),
    ) {
        DetailTopBar(
            title = p?.companyName ?: p?.name ?: "Profil",
            onBack = onBack,
            isFavorited = null,
            onToggleFavorite = null,
        )

        when {
            loading -> Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
            ) { Text(text = "Yükleniyor...", color = Color.Gray) }
            error != null -> Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
            ) { Text(text = error ?: "Hata", color = MaterialTheme.colorScheme.error) }
            p != null -> Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            val initials = (p.companyName ?: p.name ?: "?").take(1).uppercase()
                            Text(
                                text = initials,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = p.companyName ?: p.name ?: "İsim yok",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                            Text(
                                text = listOfNotNull(roleLabel(p.role), p.city).joinToString(" · "),
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                            )
                        }
                    }
                }

                FigmaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "İletişim & Bilgiler", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        DetailRow("Yetkili", p.name)
                        DetailRow("Şirket", p.companyName)
                        DetailRow("Şehir", p.city)
                        DetailRow("Adres", p.addressLine)
                        DetailRow("Hesap tipi", p.accountType)
                        DetailRow("E-posta doğrulanmış", if (p.emailVerified) "Evet" else "Hayır")
                        DetailRow("Şirket doğrulanmış", if (p.businessVerified) "Evet" else "Hayır")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                FigmaPrimaryButton(
                    text = "Mesaj at",
                    onClick = { onMessage(p.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun roleLabel(role: String?): String? = when (role) {
    "SLAUGHTERHOUSE" -> "Kesimhane"
    "ANIMAL_SELLER" -> "Hayvan Satıcısı"
    "MEAT_BUYER" -> "Et Alıcısı"
    "ADMIN" -> "Yönetici"
    else -> null
}
