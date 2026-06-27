package com.derdimet.mobil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun DerdimScreenState(
    loading: Boolean,
    error: String?,
    empty: Boolean,
    modifier: Modifier = Modifier,
    emptyTitle: String = "Henüz içerik yok",
    emptyMessage: String = "Kayıt bulunamadı.",
    offlineHint: String? = null,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when {
        loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(Modifier.size(36.dp), color = DerdimColors.Primary)
                    Text("Yükleniyor…", color = DerdimColors.MutedForeground, fontSize = 14.sp)
                }
            }
        }
        error != null -> {
            Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Bir sorun oluştu", fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, textAlign = TextAlign.Center)
                    offlineHint?.let {
                        Text(it, color = DerdimColors.MutedForeground, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                    onRetry?.let { retry ->
                        OutlinedButton(onClick = retry) { Text("Tekrar dene") }
                    }
                }
            }
        }
        empty -> {
            Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(emptyTitle, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    Text(emptyMessage, color = DerdimColors.MutedForeground, fontSize = 14.sp, textAlign = TextAlign.Center)
                    offlineHint?.let {
                        Text(it, color = DerdimColors.Amber600, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        else -> content()
    }
}
