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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.derdimet.mobil.model.ConversationOfferDto
import com.derdimet.mobil.model.MessageDto
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.InitialsAvatar
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.formatNumber

@Composable
fun ChatScreen(
    marketService: MarketService,
    conversationId: Long,
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf<List<MessageDto>>(emptyList()) }
    var text by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var myUserId by remember { mutableStateOf<Long?>(null) }
    var latestOffer by remember { mutableStateOf<ConversationOfferDto?>(null) }
    var actionHint by remember { mutableStateOf<String?>(null) }
    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.fetchMessages(conversationId)
        if (res.success) {
            messages = res.data ?: emptyList()
        } else {
            error = res.message ?: "Mesajlar alınamadı"
        }
        isLoading = false
    }

    LaunchedEffect(conversationId) {
        val meRes = marketService.fetchMe()
        if (meRes.success && meRes.data != null) {
            myUserId = meRes.data.id.toLong()
        }
        val offersRes = marketService.fetchConversationOffers(conversationId)
        if (offersRes.success) {
            latestOffer = offersRes.data?.firstOrNull()
        }
        refresh()
    }

    val offer = latestOffer
    val listingTitle = offer?.title
    val offerStatusLabel = offer?.status?.let { statusLabel(it) }
    val offerAmount = offer?.pricePerKg?.let { formatNumber(it) }

    Column(modifier = Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri") }
                    InitialsAvatar(name = title, size = 36)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(subtitle ?: "Mesajlaşma", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                    }
                    IconButton(onClick = { actionHint = "Telefon görüşmesi için profil sayfasını kullanın." }) {
                        Icon(Icons.Default.Phone, null, tint = DerdimColors.MutedForeground)
                    }
                    IconButton(onClick = { actionHint = "Sohbet menüsü yakında." }) { Icon(Icons.Default.MoreVert, null, tint = DerdimColors.MutedForeground) }
                }
                if (!listingTitle.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(DerdimColors.Muted.copy(0.7f), RoundedCornerShape(12.dp)).padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Inventory2, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp))
                            Text(listingTitle, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                        }
                        offerStatusLabel?.let {
                            Text(it, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DerdimColors.Amber700, modifier = Modifier.background(DerdimColors.Amber100, RoundedCornerShape(999.dp)).padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        actionHint?.let {
            Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        if (!offerAmount.isNullOrBlank()) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(Modifier.fillMaxWidth(0.85f).background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, DerdimColors.Border, RoundedCornerShape(16.dp)).padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                        Text("Teklif Detayı", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
                    }
                    offer.subtitle?.let { Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 4.dp)) }
                    Text("$offerAmount ₺/kg", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DerdimColors.Primary, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground, modifier = Modifier.padding(16.dp))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            else -> LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(messages, key = { it.id }) { m ->
                    ChatBubble(message = m, isMine = myUserId != null && m.senderId == myUserId)
                }
            }
        }

        Surface(color = Color.White, shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { actionHint = "Dosya ekleme yakında eklenecek." }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Dosya", tint = DerdimColors.MutedForeground)
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mesajınızı yazın...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = DerdimColors.Border,
                        focusedBorderColor = DerdimColors.Primary.copy(alpha = 0.4f),
                    ),
                )
                IconButton(
                    onClick = { if (text.isNotBlank()) sending = true },
                    enabled = !sending && text.isNotBlank(),
                    modifier = Modifier.size(44.dp).background(DerdimColors.Primary, CircleShape),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gönder", tint = Color.White)
                }
            }
        }
    }

    LaunchedEffect(sending) {
        if (!sending) return@LaunchedEffect
        val msg = text.trim()
        val res = marketService.sendMessage(conversationId, msg)
        if (res.success) {
            text = ""
            refresh()
        } else {
            error = res.message ?: "Mesaj gönderilemedi"
        }
        sending = false
    }
}

private fun statusLabel(status: OfferStatus): String = when (status) {
    OfferStatus.PENDING -> "Bekliyor"
    OfferStatus.ACCEPTED -> "Kabul"
    OfferStatus.REJECTED -> "Reddedildi"
}

@Composable
private fun ChatBubble(message: MessageDto, isMine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
    ) {
        if (!isMine) {
            Text(
                text = message.senderName ?: "",
                fontSize = 11.sp,
                color = DerdimColors.MutedForeground,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .background(
                    color = if (isMine) DerdimColors.Primary else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMine) 16.dp else 4.dp,
                        bottomEnd = if (isMine) 4.dp else 16.dp,
                    ),
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                Text(
                    text = message.text,
                    color = if (isMine) Color.White else DerdimColors.Foreground,
                    fontSize = 14.sp,
                )
                Text(
                    text = message.createdAt.take(16),
                    fontSize = 10.sp,
                    color = if (isMine) Color.White.copy(alpha = 0.75f) else DerdimColors.MutedForeground,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
