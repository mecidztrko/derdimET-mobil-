package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.NotificationSummaryDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun NotificationsScreen(
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var summary by remember { mutableStateOf<NotificationSummaryDto?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loading = true
        val res = marketService.fetchNotificationSummary()
        if (res.success) summary = res.data else error = res.message ?: "Özet alınamadı"
        loading = false
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Bildirimler", showBack = true, onBack = onBack)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when {
                loading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                summary != null -> {
                    val s = summary!!
                    NotificationRow(Icons.Default.Campaign, "Bekleyen teklifler", s.pendingOffers)
                    NotificationRow(Icons.Default.Inventory2, "Gelen teklifler", s.pendingIncoming)
                    NotificationRow(Icons.Default.Inventory2, "Alım talebi teklifleri", s.pendingPurchaseOffers)
                    NotificationRow(Icons.Default.Chat, "Okunmamış mesajlar", s.unreadMessages)
                    s.primaryLink?.let {
                        Text("Önerilen: $it", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(icon: ImageVector, label: String, count: Int) {
    Row(
        Modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, null, tint = DerdimColors.Primary)
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Text(
            "$count",
            fontWeight = FontWeight.Bold,
            color = if (count > 0) DerdimColors.Primary else DerdimColors.MutedForeground,
            modifier = Modifier
                .background(
                    if (count > 0) DerdimColors.Primary.copy(0.1f) else DerdimColors.Muted.copy(0.5f),
                    RoundedCornerShape(999.dp),
                )
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
