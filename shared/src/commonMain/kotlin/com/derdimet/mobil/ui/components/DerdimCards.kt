package com.derdimet.mobil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.ListingReview
import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.model.SellerAnimalListingDto
import androidx.compose.material.icons.filled.Star
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.ui.theme.DerdimTypeStyle
import com.derdimet.mobil.ui.theme.avatarPalette
import com.derdimet.mobil.util.formatNumber
import kotlin.math.abs

@Composable
fun DerdimListingCard(
    item: MeatSaleRequestDto,
    index: Int,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onOfferClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stripColor = DerdimTypeStyle.stripColor(item.meatType)
    val (avatarBg, avatarFg) = avatarPalette(index)
    val imageUrl = item.imageUrls.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DerdimColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(stripColor))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .background(Color(0xFFE2E8F0)),
        ) {
            if (imageUrl != null) {
                AsyncImage(model = imageUrl, contentDescription = item.title, modifier = Modifier.fillMaxWidth().height(144.dp), contentScale = ContentScale.Crop)
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(36.dp).background(Color.White.copy(0.92f), CircleShape),
            ) {
                Icon(
                    if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorited) Color(0xFFE05C2A) else DerdimColors.MutedForeground,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (item.imageUrls.size > 1) {
                Text(
                    "+${item.imageUrls.size - 1} fotoğraf",
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(Color.Black.copy(0.55f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 11.sp,
                )
            }
        }

        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(avatarBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(item.meatType.take(1).uppercase(), color = avatarFg, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                        Icon(Icons.Default.CheckCircle, null, tint = DerdimColors.Success, modifier = Modifier.size(14.dp).padding(start = 4.dp))
                    }
                    Text(
                        item.slaughterhouseCompanyName ?: item.slaughterhouseName ?: "Satıcı",
                        fontSize = 12.sp,
                        color = DerdimColors.MutedForeground,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(DerdimColors.Muted.copy(0.5f), RoundedCornerShape(12.dp)).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DerdimSpecCell("Tür", item.meatType)
                DerdimSpecCell("Stok", "${item.quantity?.let { formatNumber(it) } ?: "-"} kg")
                DerdimSpecCell("Fiyat", "${item.pricePerKg?.let { formatNumber(it) } ?: "-"} ₺/kg")
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp))
                    Text(item.location ?: item.slaughterhouseCity ?: "—", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 4.dp))
                }
                FigmaPrimaryButton(text = "Teklif Ver", onClick = onOfferClick, modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun DerdimSpecCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = DerdimColors.MutedForeground)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DerdimColors.Foreground)
    }
}

data class OfferCardData(
    val id: Long,
    val listingTitle: String,
    val partyName: String,
    val partyCompany: String?,
    val partyInitials: String,
    val offerAmount: Double?,
    val originalPrice: Double?,
    val quantityLabel: String?,
    val status: OfferStatus,
    val dateLabel: String,
    val city: String?,
    val index: Int = 0,
)

@Composable
fun DerdimOfferCard(
    offer: OfferCardData,
    showActions: Boolean,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    onMessage: (() -> Unit)? = null,
) {
    val cfg = when (offer.status) {
        OfferStatus.PENDING -> Triple(DerdimColors.Amber400, DerdimColors.Amber50, DerdimColors.Amber700)
        OfferStatus.ACCEPTED -> Triple(DerdimColors.Green400, DerdimColors.Green50, DerdimColors.Green700)
        OfferStatus.REJECTED -> Triple(DerdimColors.Red400, DerdimColors.Red50, DerdimColors.Red700)
    }
    val (strip, badgeBg, badgeFg) = cfg
    val (avatarBg, avatarFg) = avatarPalette(offer.index)
    val offerAmt = offer.offerAmount ?: 0.0
    val origAmt = offer.originalPrice ?: offerAmt
    val isHigher = offerAmt > origAmt
    val diff = abs(offerAmt - origAmt)
    val diffPct = if (origAmt > 0) (diff / origAmt * 100) else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp)),
    ) {
        Box(Modifier.fillMaxWidth().height(6.dp).background(strip))
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(40.dp).background(avatarBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text(offer.partyInitials, color = avatarFg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Column(Modifier.weight(1f)) {
                    Text(offer.listingTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${offer.partyName}${offer.partyCompany?.let { " · $it" } ?: ""}", fontSize = 12.sp, color = DerdimColors.MutedForeground)
                }
                Text(
                    when (offer.status) {
                        OfferStatus.PENDING -> "Bekliyor"
                        OfferStatus.ACCEPTED -> "Kabul"
                        OfferStatus.REJECTED -> "Reddedildi"
                    },
                    modifier = Modifier.background(badgeBg, RoundedCornerShape(12.dp)).border(1.dp, strip.copy(0.4f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                    color = badgeFg,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DerdimOfferMetric("Teklif", "${formatNumber(offerAmt)} ₺", isHigher, Modifier.weight(1f))
                DerdimOfferMetric("İstenen", "${formatNumber(origAmt)} ₺", modifier = Modifier.weight(1f))
                DerdimOfferMetric("Miktar", offer.quantityLabel ?: "—", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.padding(top = 8.dp).background(if (isHigher) DerdimColors.Green50 else DerdimColors.Red50, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(if (isHigher) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown, null, tint = if (isHigher) DerdimColors.Green600 else DerdimColors.Red600, modifier = Modifier.size(14.dp))
                Text(
                    "${if (isHigher) "+" else "-"}${formatNumber(diff)} ₺ (${String.format("%.1f", diffPct)}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isHigher) DerdimColors.Green700 else DerdimColors.Red700,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = DerdimColors.MutedForeground, modifier = Modifier.size(14.dp))
                    Text("${offer.dateLabel}${offer.city?.let { " · $it" } ?: ""}", fontSize = 11.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 4.dp))
                }
                if (showActions) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (offer.status == OfferStatus.PENDING && onReject != null && onAccept != null) {
                            Text(
                                "Reddet",
                                modifier = Modifier.clickable(onClick = onReject).background(DerdimColors.Red50, RoundedCornerShape(12.dp)).border(1.dp, DerdimColors.Red100, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                                color = DerdimColors.Red600,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                "Kabul Et",
                                modifier = Modifier.clickable(onClick = onAccept).background(DerdimColors.Green500, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else if (offer.status != OfferStatus.PENDING && onMessage != null) {
                            Text(
                                "Mesaj",
                                modifier = Modifier.clickable(onClick = onMessage).background(DerdimColors.Primary.copy(0.05f), RoundedCornerShape(12.dp)).border(1.dp, DerdimColors.Primary.copy(0.3f), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                                color = DerdimColors.Primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DerdimOfferMetric(label: String, value: String, trendingUp: Boolean? = null, modifier: Modifier = Modifier) {
    Column(modifier.background(DerdimColors.Muted.copy(0.5f), RoundedCornerShape(12.dp)).padding(10.dp)) {
        Text(label, fontSize = 10.sp, color = DerdimColors.MutedForeground)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (label == "Teklif") DerdimColors.Primary else DerdimColors.Foreground)
            trendingUp?.let {
                Icon(if (it) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown, null, tint = if (it) DerdimColors.Green500 else DerdimColors.Red400, modifier = Modifier.size(12.dp).padding(start = 2.dp))
            }
        }
    }
}

@Composable
fun DerdimConversationRow(
    index: Int,
    name: String,
    company: String?,
    listingTitle: String?,
    lastMessage: String?,
    time: String?,
    unread: Int = 0,
    online: Boolean = false,
    onClick: () -> Unit,
) {
    val (avatarBg, avatarFg) = avatarPalette(index)
    val initials = initialsFrom(name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Box(Modifier.size(48.dp).background(avatarBg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Text(initials, color = avatarFg, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            if (online) {
                Box(Modifier.align(Alignment.BottomEnd).size(12.dp).background(DerdimColors.Green500, CircleShape).border(2.dp, Color.White, CircleShape))
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = if (unread > 0) FontWeight.Bold else FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                time?.let { Text(it, fontSize = 11.sp, color = DerdimColors.MutedForeground) }
            }
            if (!company.isNullOrBlank() || !listingTitle.isNullOrBlank()) {
                Text(listOfNotNull(company, listingTitle).joinToString(" · "), fontSize = 11.sp, color = DerdimColors.MutedForeground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            lastMessage?.let {
                Text(it, fontSize = 12.sp, color = if (unread > 0) DerdimColors.Foreground else DerdimColors.MutedForeground, fontWeight = if (unread > 0) FontWeight.Medium else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Normal)
            }
        }
        if (unread > 0) {
            Box(Modifier.size(20.dp).background(DerdimColors.Primary, CircleShape), contentAlignment = Alignment.Center) {
                Text(if (unread > 9) "9+" else unread.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Icon(Icons.Default.ChevronRight, null, tint = DerdimColors.MutedForeground.copy(0.6f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun DerdimStatsRow(
    pending: Int,
    accepted: Int,
    rejected: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DerdimStatBox(pending, "Bekleyen", DerdimColors.Amber50, DerdimColors.Amber600, DerdimColors.Amber100, Modifier.weight(1f))
        DerdimStatBox(accepted, "Kabul", DerdimColors.Green50, DerdimColors.Green600, DerdimColors.Green100, Modifier.weight(1f))
        DerdimStatBox(rejected, "Reddedilen", DerdimColors.Red50, DerdimColors.Red600, DerdimColors.Red100, Modifier.weight(1f))
    }
}

@Composable
private fun DerdimStatBox(count: Int, label: String, bg: Color, fg: Color, border: Color, modifier: Modifier = Modifier) {
    Column(
        modifier.background(bg, RoundedCornerShape(12.dp)).border(1.dp, border, RoundedCornerShape(12.dp)).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("$count", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = fg)
        Text(label, fontSize = 11.sp, color = DerdimColors.MutedForeground)
    }
}

@Composable
fun DerdimAnimalPurchaseCard(
    item: AnimalPurchaseRequestDto,
    index: Int,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onOfferClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stripColor = when (item.animalCategory) {
        AnimalCategory.KUCUKBAS -> Color(0xFF10B981)
        AnimalCategory.BUYUKBAS -> Color(0xFF3B82F6)
        null -> DerdimColors.Primary
    }
    val (avatarBg, avatarFg) = avatarPalette(index)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DerdimColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(stripColor))
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(40.dp).background(avatarBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text((item.slaughterhouseCompanyName ?: item.slaughterhouseName ?: "K").take(1).uppercase(), color = avatarFg, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${item.slaughterhouseCompanyName ?: item.slaughterhouseName ?: "Kesimhane"} • ${item.slaughterhouseCity ?: "—"}",
                        fontSize = 12.sp,
                        color = DerdimColors.MutedForeground,
                    )
                }
                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorited) Color(0xFFE05C2A) else DerdimColors.MutedForeground,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().background(DerdimColors.Muted.copy(0.5f), RoundedCornerShape(12.dp)).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DerdimSpecCell("Kategori", item.animalCategory?.name ?: "—")
                DerdimSpecCell("Adet", "${item.quantity ?: "-"}")
                DerdimSpecCell("Ağırlık", item.expectedWeight?.let { "${formatNumber(it)} kg" } ?: "—")
            }
            item.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Alım talebi", fontSize = 11.sp, color = DerdimColors.MutedForeground)
                FigmaPrimaryButton(text = "Teklif Ver", onClick = onOfferClick, modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DerdimAnimalListingCard(
    item: SellerAnimalListingDto,
    index: Int,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onOfferClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stripColor = when (item.category) {
        AnimalCategory.KUCUKBAS -> Color(0xFF10B981)
        AnimalCategory.BUYUKBAS -> Color(0xFF3B82F6)
    }
    val (avatarBg, avatarFg) = avatarPalette(index)
    val imageUrl = item.imageUrls.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DerdimColors.Border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(stripColor))
        if (imageUrl != null) {
            AsyncImage(model = imageUrl, contentDescription = item.type, modifier = Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Crop)
        }
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(40.dp).background(avatarBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text(item.type.take(1).uppercase(), color = avatarFg, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text("${item.type} · ${item.category.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${item.sellerCompanyName ?: item.sellerName ?: "Satıcı"} • ${item.location ?: item.sellerCity ?: "—"}",
                        fontSize = 12.sp,
                        color = DerdimColors.MutedForeground,
                    )
                }
                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorited) Color(0xFFE05C2A) else DerdimColors.MutedForeground,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().background(DerdimColors.Muted.copy(0.5f), RoundedCornerShape(12.dp)).padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DerdimSpecCell("Yaş", item.ageMonths?.let { "$it ay" } ?: "—")
                DerdimSpecCell("Adet", "${item.quantity}")
                DerdimSpecCell("Fiyat", item.price?.let { "${formatNumber(it)} ₺" } ?: "—")
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.breed ?: "Hayvan ilanı", fontSize = 11.sp, color = DerdimColors.MutedForeground, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                FigmaPrimaryButton(text = "Teklif Ver", onClick = onOfferClick, modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DerdimReviewsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text("Değerlendirmeler", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Text(
            "Kullanıcı değerlendirmeleri yakında eklenecek.",
            fontSize = 13.sp,
            color = DerdimColors.MutedForeground,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun DerdimReviewsSection(
    rating: Double,
    reviewCount: Int,
    reviews: List<ListingReview>,
    modifier: Modifier = Modifier,
) {
    if (reviews.isEmpty()) return
    val ratingDist = reviews.groupingBy { it.rating }.eachCount()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Değerlendirmeler", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                Text(String.format("%.1f", rating), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                Text("($reviewCount)", fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(start = 4.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        (5 downTo 1).forEach { star ->
            val count = ratingDist[star] ?: 0
            val pct = if (reviews.isNotEmpty()) count.toFloat() / reviews.size else 0f
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("$star", fontSize = 11.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(end = 6.dp))
                Box(Modifier.weight(1f).height(6.dp).background(DerdimColors.Muted, RoundedCornerShape(999.dp))) {
                    Box(Modifier.fillMaxWidth(pct).fillMaxHeight().background(Color(0xFFFBBF24), RoundedCornerShape(999.dp)))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        reviews.forEach { review ->
            Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(review.authorName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.weight(1f))
                    Text(review.timeAgo, fontSize = 11.sp, color = DerdimColors.MutedForeground)
                }
                Row(Modifier.padding(top = 4.dp)) {
                    repeat(5) { i ->
                        Icon(Icons.Default.Star, null, tint = if (i < review.rating) Color(0xFFFBBF24) else DerdimColors.Muted, modifier = Modifier.size(12.dp))
                    }
                }
                Text(review.comment, fontSize = 13.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 6.dp))
            }
        }
        if (reviewCount > reviews.size) {
            Text(
                "Tüm değerlendirmeleri gör ($reviewCount)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(DerdimColors.Primary.copy(0.05f), RoundedCornerShape(12.dp))
                    .border(1.dp, DerdimColors.Primary.copy(0.3f), RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp),
                color = DerdimColors.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
fun DerdimFormCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, DerdimColors.Border.copy(0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DerdimColors.Foreground)
        subtitle?.let {
            Text(it, fontSize = 12.sp, color = DerdimColors.MutedForeground, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
        } ?: Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun DerdimFilterTabs(
    tabs: List<Triple<String, String, Int>>,
    selectedKey: String,
    onSelect: (String) -> Unit,
) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEach { (key, label, count) ->
            val active = selectedKey == key
            Row(
                modifier = Modifier
                    .clickable { onSelect(key) }
                    .background(if (active) DerdimColors.Primary else Color.White, RoundedCornerShape(12.dp))
                    .border(2.dp, if (active) DerdimColors.Primary else DerdimColors.Border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(label, color = if (active) Color.White else DerdimColors.MutedForeground, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    "$count",
                    modifier = Modifier.background(if (active) Color.White.copy(0.2f) else DerdimColors.Muted, RoundedCornerShape(999.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                    color = if (active) Color.White else DerdimColors.MutedForeground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
