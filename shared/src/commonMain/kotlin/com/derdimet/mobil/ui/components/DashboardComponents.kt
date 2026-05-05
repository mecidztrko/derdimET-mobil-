package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardTopBar(title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(Color(0xFFEEF2FF), Color(0xFFE0F2FE))
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardStatusBadge(text: String, tone: StatusTone) {
    val (bg, fg) = when (tone) {
        StatusTone.Neutral -> Color(0xFFE2E8F0) to Color(0xFF334155)
        StatusTone.Info -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        StatusTone.Success -> Color(0xFFDCFCE7) to Color(0xFF166534)
        StatusTone.Warning -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        StatusTone.Danger -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
    }
    Box(
        modifier = Modifier
            .background(color = bg, shape = RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DashboardInlineMessage(text: String, tone: StatusTone, modifier: Modifier = Modifier) {
    val (bg, fg) = when (tone) {
        StatusTone.Neutral -> Color(0xFFF1F5F9) to Color(0xFF334155)
        StatusTone.Info -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
        StatusTone.Success -> Color(0xFFECFDF5) to Color(0xFF047857)
        StatusTone.Warning -> Color(0xFFFFFBEB) to Color(0xFF92400E)
        StatusTone.Danger -> Color(0xFFFEF2F2) to Color(0xFFB91C1C)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Text(text = text, color = fg, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun DashboardLoadingState(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(end = 10.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}

@Composable
fun DashboardEmptyState(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

enum class StatusTone {
    Neutral, Info, Success, Warning, Danger
}
