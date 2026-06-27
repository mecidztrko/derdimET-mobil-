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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.UpdateNotificationPreferencesRequest
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFormCard
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlinx.coroutines.launch

@Composable
fun NotificationPreferencesScreen(
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var pushOffersEnabled by remember { mutableStateOf(true) }
    var pushMessagesEnabled by remember { mutableStateOf(true) }
    var pushMarketingEnabled by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refreshKey) {
        loading = true
        error = null
        val res = marketService.fetchNotificationPreferences()
        if (res.success && res.data != null) {
            pushOffersEnabled = res.data.pushOffersEnabled
            pushMessagesEnabled = res.data.pushMessagesEnabled
            pushMarketingEnabled = res.data.pushMarketingEnabled
        } else {
            error = res.message ?: "Tercihler yüklenemedi"
        }
        loading = false
    }

    fun savePreference(update: UpdateNotificationPreferencesRequest) {
        if (saving) return
        saving = true
        error = null
        scope.launch {
            val res = marketService.updateNotificationPreferences(update)
            saving = false
            if (res.success && res.data != null) {
                pushOffersEnabled = res.data.pushOffersEnabled
                pushMessagesEnabled = res.data.pushMessagesEnabled
                pushMarketingEnabled = res.data.pushMarketingEnabled
            } else {
                error = res.message ?: "Tercih kaydedilemedi"
            }
        }
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Bildirim Tercihleri", showBack = true, onBack = onBack)
        DerdimScreenState(
            loading = loading,
            error = error,
            empty = false,
            onRetry = { refreshKey++ },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Push bildirimlerini hangi konularda almak istediğinizi seçin.",
                    fontSize = 14.sp,
                    color = DerdimColors.MutedForeground,
                )
                DerdimFormCard(title = "Push Bildirimleri", subtitle = if (saving) "Kaydediliyor..." else null) {
                        PreferenceSwitch(
                            label = "Teklif bildirimleri",
                            description = "Yeni teklifler ve teklif durumu güncellemeleri",
                            checked = pushOffersEnabled,
                            enabled = !saving,
                            onCheckedChange = { checked ->
                                pushOffersEnabled = checked
                                savePreference(UpdateNotificationPreferencesRequest(pushOffersEnabled = checked))
                            },
                        )
                        PreferenceSwitch(
                            label = "Mesaj bildirimleri",
                            description = "Yeni mesajlar ve sohbet güncellemeleri",
                            checked = pushMessagesEnabled,
                            enabled = !saving,
                            onCheckedChange = { checked ->
                                pushMessagesEnabled = checked
                                savePreference(UpdateNotificationPreferencesRequest(pushMessagesEnabled = checked))
                            },
                        )
                        PreferenceSwitch(
                            label = "Kampanya bildirimleri",
                            description = "Duyurular ve pazarlama mesajları",
                            checked = pushMarketingEnabled,
                            enabled = !saving,
                            onCheckedChange = { checked ->
                                pushMarketingEnabled = checked
                                savePreference(UpdateNotificationPreferencesRequest(pushMarketingEnabled = checked))
                            },
                        )
                    }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun PreferenceSwitch(
    label: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(DerdimColors.Muted.copy(0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(description, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 2.dp))
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
