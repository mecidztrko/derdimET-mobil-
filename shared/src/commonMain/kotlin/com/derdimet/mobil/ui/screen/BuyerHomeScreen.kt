package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.ui.components.DashboardEmptyState

@Composable
fun BuyerHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Et Alıcı Paneli", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            text = "Tedarik sürecini tek ekrandan takip edin.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
        )

        BuyerHeroCard()
        Spacer(modifier = Modifier.height(14.dp))

        BuyerQuickInfoCard(
            title = "Aktif siparişler",
            subtitle = "Henüz aktif siparişiniz bulunmuyor.",
            icon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color(0xFF2563EB)) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        BuyerQuickInfoCard(
            title = "Son satın almalar",
            subtitle = "Geçmiş alımlarınız burada listelenecek.",
            icon = { Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color(0xFF7C3AED)) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        BuyerQuickInfoCard(
            title = "Favori satıcılar",
            subtitle = "Güvendiğiniz üreticileri favorileyin, hızlı teklif alın.",
            icon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B)) }
        )
    }
}

@Composable
private fun BuyerHeroCard() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFEEF2FF), Color(0xFFE0F2FE))
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Bugün için öneri",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1D4ED8)
                )
                Text(
                    text = "Keşfet sekmesinden satıcı tekliflerini karşılaştırarak en uygun fiyatı seçin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun BuyerQuickInfoCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                icon()
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            Box(modifier = Modifier.padding(top = 8.dp)) {
                DashboardEmptyState(title = "Durum", description = subtitle)
            }
        }
    }
}
