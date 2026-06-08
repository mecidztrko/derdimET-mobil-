package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.util.formatNumber

fun initialsFrom(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    return name.trim().split(Regex("\\s+"))
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifBlank { "?" }
}

@Composable
fun InitialsAvatar(
    name: String?,
    modifier: Modifier = Modifier,
    size: Int = 44,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1B3A6B), Color(0xFF1D5BE6)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsFrom(name),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size / 3).sp,
        )
    }
}

@Composable
fun OfferStatusBadge(status: OfferStatus?, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when (status) {
        OfferStatus.PENDING -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Bekliyor")
        OfferStatus.ACCEPTED -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), "Kabul")
        OfferStatus.REJECTED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Reddedildi")
        null -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "—")
    }
    Text(
        text = label,
        modifier = modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun MarketplaceScreenHeader(
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            subtitle?.let {
                Text(text = it, fontSize = 13.sp, color = FigmaStyle.MutedText, modifier = Modifier.padding(top = 2.dp))
            }
        }
        badgeText?.let {
            Text(
                text = it,
                modifier = Modifier
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                color = Color(0xFFB45309),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun MarketplaceSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onFilterClick: () -> Unit,
    activeFilterCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8))
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
        )
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
        ) {
            Box {
                Icon(Icons.Default.Tune, contentDescription = "Filtre", tint = MaterialTheme.colorScheme.primary)
                if (activeFilterCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .background(Color(0xFFE05C2A), CircleShape),
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipRow(
    chips: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            val active = selected == chip || (selected == null && chip == "Tümü")
            Text(
                text = chip,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) MaterialTheme.colorScheme.primary else Color.White)
                    .border(1.dp, if (active) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
                    .clickable { onSelect(chip) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                color = if (active) Color.White else Color(0xFF334155),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun MeatListingCard(
    item: MeatSaleRequestDto,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onOfferClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageUrl = item.imageUrls.firstOrNull()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFFE2E8F0)),
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape),
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (isFavorited) Color(0xFFE05C2A) else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp),
                )
            }
            if (item.imageUrls.size > 1) {
                Text(
                    text = "+${item.imageUrls.size - 1} fotoğraf",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 11.sp,
                )
            }
        }

        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                InitialsAvatar(
                    name = item.slaughterhouseCompanyName ?: item.slaughterhouseName,
                    size = 36,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = item.slaughterhouseCompanyName ?: item.slaughterhouseName ?: "Kesimhane",
                        fontSize = 12.sp,
                        color = FigmaStyle.MutedText,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SpecCell("Tür", item.meatType)
                SpecCell("Stok", "${item.quantity?.let { formatNumber(it) } ?: "-"} kg")
                SpecCell("Fiyat", "${item.pricePerKg?.let { formatNumber(it) } ?: "-"} ₺/kg")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                    Text(
                        text = item.location ?: item.slaughterhouseCity ?: "—",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                    )
                }
                FigmaPrimaryButton(
                    text = "Teklif Ver",
                    onClick = onOfferClick,
                    modifier = Modifier.height(40.dp),
                )
            }
        }
    }
}

@Composable
private fun SpecCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
    }
}

@Composable
fun ConversationListItem(
    conversation: ConversationItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(name = conversation.otherUserName ?: conversation.otherUserEmail, size = 48)
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = conversation.otherUserName ?: conversation.otherUserEmail ?: "Kullanıcı",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                conversation.lastMessageAt?.let {
                    Text(text = it.take(10), fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }
            conversation.otherUserRole?.let {
                Text(text = it, fontSize = 12.sp, color = Color(0xFF94A3B8), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
    }
}

@Composable
fun ProfileStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun DetailScreenTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color(0xFF0F172A),
        )
        trailing?.invoke()
    }
}
