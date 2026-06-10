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
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.MarketplaceSearchBar
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun MessagesInboxScreen(marketService: MarketService, refreshKey: Int = 0) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var conversations by remember { mutableStateOf<List<ConversationItemDto>>(emptyList()) }
    var lastMessagePreview by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var query by remember { mutableStateOf("") }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var openProfileUserId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(refreshKey) {
        isLoading = true
        error = null
        val res = marketService.fetchConversations()
        if (res.success) {
            conversations = res.data ?: emptyList()
            val previews = mutableMapOf<Long, String>()
            conversations.take(15).forEach { convo ->
                val msgRes = marketService.fetchMessages(convo.conversationId)
                if (msgRes.success) {
                    msgRes.data?.lastOrNull()?.text?.let { previews[convo.conversationId] = it }
                }
            }
            lastMessagePreview = previews
        } else {
            error = res.message ?: "Mesajlar alınamadı"
        }
        isLoading = false
    }

    val profileUserId = openProfileUserId
    if (profileUserId != null) {
        PublicProfileScreen(
            userId = profileUserId,
            marketService = marketService,
            onBack = { openProfileUserId = null },
            onMessage = { id ->
                openProfileUserId = null
                val existing = conversations.find { it.otherUserId == id }
                if (existing != null) {
                    selectedConversation = existing
                }
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

    val unreadTotal = conversations.sumOf { it.unreadCount }
    val filtered = remember(conversations, query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) conversations
        else conversations.filter {
            (it.otherUserName ?: "").lowercase().contains(q) ||
                (it.otherUserEmail ?: "").lowercase().contains(q) ||
                (it.otherUserRole ?: "").lowercase().contains(q)
        }
    }

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
                when {
                    isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                    error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                    filtered.isEmpty() -> Text("Henüz mesaj yok.", color = DerdimColors.MutedForeground)
                    else -> LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(filtered, key = { _, it -> it.conversationId }) { index, item ->
                            DerdimConversationRow(
                                index = index,
                                name = item.otherUserName ?: item.otherUserEmail ?: "Kullanıcı",
                                company = roleLabelTr(item.otherUserRole),
                                listingTitle = null,
                                lastMessage = lastMessagePreview[item.conversationId] ?: if (item.lastMessageAt != null) "Mesajlaşmaya devam et" else null,
                                time = item.lastMessageAt?.take(16),
                                unread = item.unreadCount,
                                online = false,
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
    "SLAUGHTERHOUSE" -> "Kesimhane"
    "ANIMAL_SELLER" -> "Hayvan Satıcı"
    "MEAT_BUYER" -> "Et Alıcı"
    else -> role
}
