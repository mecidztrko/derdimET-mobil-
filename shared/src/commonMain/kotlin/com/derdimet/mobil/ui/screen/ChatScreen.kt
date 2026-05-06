package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.MessageDto
import com.derdimet.mobil.service.MarketService

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

    LaunchedEffect(conversationId) { refresh() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("Geri") }
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "")
        }

        when {
            isLoading -> Text("Yükleniyor...", color = Color.Gray)
            error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages) { m ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = m.senderName ?: m.senderId.toString(), fontWeight = FontWeight.SemiBold)
                            Text(text = m.text, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mesaj") },
        )
        Button(
            onClick = {
                if (text.isBlank() || sending) return@Button
                sending = true
            },
            enabled = !sending,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) { Text(if (sending) "Gönderiliyor..." else "Gönder") }

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

