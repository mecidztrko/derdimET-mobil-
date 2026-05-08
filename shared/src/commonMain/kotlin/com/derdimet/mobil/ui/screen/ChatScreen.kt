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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.derdimet.mobil.model.MessageDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.FigmaCard
import com.derdimet.mobil.ui.components.FigmaPrimaryButton
import com.derdimet.mobil.ui.components.FigmaSecondaryButton
import com.derdimet.mobil.ui.components.FigmaStyle

@Composable
fun ChatScreen(
    marketService: MarketService,
    conversationId: Long,
    title: String,
    onBack: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf<List<MessageDto>>(emptyList()) }
    var text by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var myUserId by remember { mutableStateOf<Long?>(null) }
    var refreshTick by remember { mutableStateOf(0) }

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
        // Fetch current user id once to align bubbles
        val meRes = marketService.fetchMe()
        if (meRes.success && meRes.data != null) {
            myUserId = meRes.data.id.toLong()
        }
        refresh()
    }

    LaunchedEffect(refreshTick) {
        if (refreshTick == 0) return@LaunchedEffect
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaStyle.ScreenBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FigmaSecondaryButton(text = "Geri", onClick = onBack)
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isLoading) "Yükleniyor..." else "Sohbet",
                        fontSize = 12.sp,
                        color = FigmaStyle.MutedText,
                    )
                }
                FigmaSecondaryButton(
                    text = "Yenile",
                    enabled = !isLoading,
                    onClick = { refreshTick++ },
                )
            }
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color(0xFF64748B))
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(messages) { m ->
                    val isMine = myUserId != null && m.senderId == myUserId
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .background(
                                    color = if (isMine) MaterialTheme.colorScheme.primary else Color.White,
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (!isMine) {
                                    Text(
                                        text = m.senderName ?: m.senderId.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                    )
                                }
                                Text(
                                    text = m.text,
                                    color = if (isMine) Color.White else Color(0xFF0F172A),
                                )
                                Text(
                                    text = m.createdAt,
                                    fontSize = 11.sp,
                                    color = if (isMine) Color.White.copy(alpha = 0.75f) else Color(0xFF94A3B8),
                                )
                            }
                        }
                    }
                }
            }
        }

        FigmaCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Mesaj") },
                    singleLine = true,
                )
                FigmaPrimaryButton(
                    text = if (sending) "..." else "Gönder",
                    enabled = !sending && text.isNotBlank(),
                    onClick = { sending = true },
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))

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
}

