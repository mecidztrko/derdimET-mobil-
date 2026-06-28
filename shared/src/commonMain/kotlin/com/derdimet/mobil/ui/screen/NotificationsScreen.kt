package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
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
import com.derdimet.mobil.model.NotificationInboxItemDto
import com.derdimet.mobil.model.NotificationType
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlinx.coroutines.launch

private val typeFilters = listOf(
    "" to "Tümü",
    "OFFER" to "Teklif",
    "PAYMENT" to "Ödeme",
    "LISTING" to "İlan",
    "MESSAGE" to "Mesaj",
)

@Composable
fun NotificationsScreen(
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var items by remember { mutableStateOf<List<NotificationInboxItemDto>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var typeFilter by remember { mutableStateOf("") }
    var unreadOnly by remember { mutableStateOf(false) }
    var markingAll by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refreshKey, typeFilter, unreadOnly) {
        loading = true
        error = null
        val res = marketService.fetchNotificationInbox(
            type = typeFilter.takeIf { it.isNotBlank() },
            unreadOnly = unreadOnly,
        )
        if (res.success) items = res.data.orEmpty() else error = res.message ?: "Bildirimler alınamadı"
        loading = false
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "Bildirimler",
            showBack = true,
            onBack = onBack,
            action = {
                FigmaPrimaryButton(
                    text = if (markingAll) "..." else "Tümünü oku",
                    onClick = {
                        if (markingAll) return@FigmaPrimaryButton
                        scope.launch {
                            markingAll = true
                            marketService.markAllNotificationsRead()
                            markingAll = false
                            refreshKey++
                        }
                    },
                    modifier = Modifier.padding(end = 4.dp),
                )
            },
        )
        DerdimListScreenBody(
            header = {
                DerdimFilterTabs(
                    tabs = typeFilters.map { (key, label) -> Triple(key, label, if (key.isBlank()) items.size else items.count { it.type.name == key }) },
                    selectedKey = typeFilter,
                    onSelect = { typeFilter = it },
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable { unreadOnly = !unreadOnly },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        if (unreadOnly) "✓ Okunmamış" else "Okunmamış",
                        fontSize = 12.sp,
                        fontWeight = if (unreadOnly) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (unreadOnly) DerdimColors.Primary else DerdimColors.MutedForeground,
                        modifier = Modifier
                            .background(
                                if (unreadOnly) DerdimColors.Primary.copy(0.1f) else Color.White,
                                RoundedCornerShape(999.dp),
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            },
            content = {
                DerdimScreenState(
                    loading = loading,
                    error = error,
                    empty = items.isEmpty(),
                    emptyTitle = "Bildirim yok",
                    emptyMessage = "Teklif, ödeme ve ilan güncellemeleri burada görünür.",
                    onRetry = { refreshKey++ },
                ) {
                    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(items, key = { it.id }) { item ->
                            NotificationInboxRow(
                                item = item,
                                onClick = {
                                    if (!item.read) {
                                        scope.launch {
                                            marketService.markNotificationRead(item.id)
                                            refreshKey++
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun NotificationInboxRow(item: NotificationInboxItemDto, onClick: () -> Unit) {
    val bg = if (item.read) Color.White else DerdimColors.Primary.copy(0.06f)
    val border = if (item.read) DerdimColors.Border.copy(0.5f) else DerdimColors.Primary.copy(0.25f)
    Row(
        Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Default.Notifications, null, tint = DerdimColors.Primary, modifier = Modifier.padding(top = 2.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(item.createdAt.take(10), fontSize = 11.sp, color = DerdimColors.MutedForeground)
            }
            item.body?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground)
            }
            Text(notificationTypeLabel(item.type), fontSize = 10.sp, color = DerdimColors.Primary)
        }
    }
}

private fun notificationTypeLabel(type: NotificationType): String = when (type) {
    NotificationType.OFFER -> "Teklif"
    NotificationType.PAYMENT -> "Ödeme"
    NotificationType.LISTING -> "İlan"
    NotificationType.MESSAGE -> "Mesaj"
    NotificationType.SYSTEM -> "Sistem"
}
