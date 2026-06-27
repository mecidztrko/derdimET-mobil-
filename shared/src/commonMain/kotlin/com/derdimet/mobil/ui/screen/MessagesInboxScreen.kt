package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimActionBadge
import com.derdimet.mobil.ui.components.DerdimConversationRow
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.MarketplaceSearchBar
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.viewmodel.MessagesInboxViewModel

@Composable
fun MessagesInboxScreen(
    viewModel: MessagesInboxViewModel,
    marketService: MarketService,
    refreshKey: Int = 0,
) {
    val uiState by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(refreshKey) {
        viewModel.load()
    }

    val profileUserId = openProfileUserId
    if (profileUserId != null) {
        PublicProfileScreen(
            userId = profileUserId,
            marketService = marketService,
            onBack = { openProfileUserId = null },
            onMessage = { id ->
                openProfileUserId = null
                val existing = uiState.conversations.find { it.otherUserId == id }
                if (existing != null) selectedConversation = existing
            },
        )
        return
    }

    val convo = selectedConversation
    if (convo != null) {
        ChatScreen(
            marketService = marketService,
            conversationId = convo.conversationId,
            title = convo.otherUserName ?: (convo.otherUserEmail ?: "Sohbet"),
            subtitle = roleLabelTr(convo.otherUserRole),
            otherUserId = convo.otherUserId,
            onBack = { selectedConversation = null },
            onOpenProfile = { openProfileUserId = it },
        )
        return
    }

    val unreadTotal = uiState.conversations.sumOf { it.unreadCount }
    val filtered = remember(uiState.conversations, query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) uiState.conversations
        else uiState.conversations.filter {
            (it.otherUserName ?: "").lowercase().contains(q) ||
                (it.otherUserEmail ?: "").lowercase().contains(q) ||
                (it.otherUserRole ?: "").lowercase().contains(q)
        }
    }
    val offlineHint = if (uiState.isOfflineCache) "Çevrimdışı — son konuşmalar gösteriliyor" else null

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "Mesajlar",
            action = { if (unreadTotal > 0) DerdimActionBadge("$unreadTotal okunmamış") },
        )
        DerdimListScreenBody(
            header = {
                MarketplaceSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Konuşma veya kişi ara...",
                    onFilterClick = {},
                    showFilterButton = false,
                )
                Text("${filtered.size} konuşma", fontSize = 12.sp, color = DerdimColors.MutedForeground)
            },
            content = {
                DerdimScreenState(
                    loading = uiState.isLoading,
                    error = if (!uiState.isLoading && uiState.conversations.isEmpty()) uiState.error else null,
                    empty = !uiState.isLoading && uiState.error == null && filtered.isEmpty(),
                    emptyTitle = "Henüz mesaj yok",
                    emptyMessage = "İlan veya teklif üzerinden sohbet başlatabilirsiniz.",
                    offlineHint = offlineHint,
                    onRetry = { viewModel.load() },
                ) {
                    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(filtered, key = { _, it -> it.conversationId }) { index, item ->
                            DerdimConversationRow(
                                index = index,
                                name = item.otherUserName ?: item.otherUserEmail ?: "Kullanıcı",
                                company = roleLabelTr(item.otherUserRole),
                                listingTitle = null,
                                lastMessage = uiState.lastMessagePreview[item.conversationId]
                                    ?: if (item.lastMessageAt != null) "Mesajlaşmaya devam et" else null,
                                time = item.lastMessageAt?.take(10),
                                unread = item.unreadCount,
                                onClick = { selectedConversation = item },
                            )
                        }
                    }
                }
            },
        )
    }
}

private fun roleLabelTr(role: String?): String? = when (role) {
    "MEAT_BUYER" -> "Et alıcı"
    "ANIMAL_SELLER" -> "Hayvan satıcı"
    "SLAUGHTERHOUSE" -> "Kesimhane"
    "ADMIN" -> "Yönetici"
    else -> role
}
