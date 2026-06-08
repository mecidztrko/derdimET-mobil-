package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.ui.theme.DerdimColors

enum class AppNavTab(val label: String, val icon: ImageVector) {
    Search("Ara", Icons.Default.Search),
    Offers("Teklifler", Icons.Default.ReceiptLong),
    Create("İlan Ver", Icons.Default.Add),
    Messages("Mesajlar", Icons.Default.Chat),
    Profile("Profil", Icons.Default.Person),
}

@Composable
fun AppBottomBar(
    tabs: List<AppNavTab>,
    selected: AppNavTab,
    onSelect: (AppNavTab) -> Unit,
    offerBadge: Int = 0,
    messageBadge: Int = 0,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DerdimColors.Card,
        shadowElevation = 12.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            tabs.forEach { tab ->
                val active = selected == tab
                val badge = when (tab) {
                    AppNavTab.Offers -> offerBadge
                    AppNavTab.Messages -> messageBadge
                    else -> 0
                }
                if (tab == AppNavTab.Create) {
                    CreateTabItem(active = active, onClick = { onSelect(tab) })
                } else {
                    StandardTabItem(
                        tab = tab,
                        active = active,
                        badge = badge,
                        onClick = { onSelect(tab) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StandardTabItem(
    tab: AppNavTab,
    active: Boolean,
    badge: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = if (active) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp),
            )
            if (badge > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(16.dp)
                        .background(Color(0xFF1D5BE6), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (badge > 9) "9+" else badge.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Text(
            text = tab.label,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            color = if (active) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
        )
        if (active) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            )
        }
    }
}

@Composable
private fun CreateTabItem(active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-8).dp)
                .shadow(
                    elevation = if (active) 10.dp else 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                )
                .size(48.dp)
                .background(
                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "İlan Ver",
                tint = if (active) Color.White else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            text = "İlan Ver",
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            color = if (active) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
            modifier = Modifier.offset(y = (-6).dp),
        )
    }
}
