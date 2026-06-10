package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun DetailGridCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier.background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(label, fontSize = 11.sp, color = DerdimColors.MutedForeground)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

/** İlan detay sayfalarında üst geri butonu ve favori toggle barı. */
@Composable
fun DetailTopBar(
    title: String,
    onBack: () -> Unit,
    isFavorited: Boolean?,
    onToggleFavorite: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
        }
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (onToggleFavorite != null) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorited == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorile",
                    tint = if (isFavorited == true) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                )
            }
        }
    }
}

/** Detay sayfasındaki yatay foto carousel (Coil ile yüklenir). */
@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
) {
    if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "📷  Görsel yok", color = Color(0xFF64748B))
        }
        return
    }
    // LazyRow, verticalScroll Column içinde kullanıldığında çökme yapar; Row + horizontalScroll güvenli.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        imageUrls.forEach { url ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE2E8F0),
                modifier = Modifier
                    .width(300.dp)
                    .height(220.dp),
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
            }
        }
    }
}

/** Detay sayfasında etiket-değer çifti gösterir. */
@Composable
fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

/** Satıcı/Kesimhane bilgisi (ilan detayda) — dokunulduğunda public profil açılır. */
@Composable
fun OwnerCard(
    name: String?,
    companyName: String?,
    city: String?,
    onClick: () -> Unit,
) {
    val displayName = companyName?.takeIf { it.isNotBlank() }
        ?: name?.takeIf { it.isNotBlank() }
        ?: "İlan sahibi"
    val avatarLetter = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = avatarLetter,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = displayName, fontWeight = FontWeight.SemiBold)
            Text(
                text = listOfNotNull(name?.takeIf { it != displayName }, city).joinToString(" · "),
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
            )
        }
        Text(text = "Detay >", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
    }
}
