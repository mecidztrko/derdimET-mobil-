package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.model.BuyerMeatOfferItemDto
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimActionBadge
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimOfferCard
import com.derdimet.mobil.ui.components.DerdimStatsRow
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.OfferCardData
import com.derdimet.mobil.ui.components.initialsFrom
import com.derdimet.mobil.ui.theme.DerdimColors

@Composable
fun BuyerMyOffersScreen(marketService: MarketService) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var offers by remember { mutableStateOf<List<BuyerMeatOfferItemDto>>(emptyList()) }
    var filter by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        val res = marketService.fetchMyBuyerMeatOffers()
        if (res.success) offers = res.data ?: emptyList() else error = res.message ?: "Teklifler alınamadı"
        isLoading = false
    }

    val pending = offers.count { it.status == OfferStatus.PENDING }
    val accepted = offers.count { it.status == OfferStatus.ACCEPTED }
    val rejected = offers.count { it.status == OfferStatus.REJECTED }
    val filtered = remember(offers, filter) {
        when (filter) {
            "pending" -> offers.filter { it.status == OfferStatus.PENDING }
            "accepted" -> offers.filter { it.status == OfferStatus.ACCEPTED }
            "rejected" -> offers.filter { it.status == OfferStatus.REJECTED }
            else -> offers
        }
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "Teklifler",
            action = { if (pending > 0) DerdimActionBadge("$pending bekliyor") },
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DerdimStatsRow(pending, accepted, rejected)
            DerdimFilterTabs(
                tabs = listOf(
                    Triple("", "Tümü", offers.size),
                    Triple("pending", "Bekleyen", pending),
                    Triple("accepted", "Kabul", accepted),
                    Triple("rejected", "Reddedilen", rejected),
                ),
                selectedKey = filter,
                onSelect = { filter = it },
            )
            when {
                isLoading -> Text("Yükleniyor...", color = DerdimColors.MutedForeground)
                error != null -> Text(error ?: "Hata", color = MaterialTheme.colorScheme.error)
                filtered.isEmpty() -> Text("Bu filtrede teklif yok.", color = DerdimColors.MutedForeground)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(filtered, key = { _, it -> it.offerId }) { index, item ->
                        DerdimOfferCard(
                            offer = item.toOfferCardData(index),
                            showActions = false,
                        )
                    }
                }
            }
        }
    }
}

private fun BuyerMeatOfferItemDto.toOfferCardData(index: Int) = OfferCardData(
    id = offerId,
    listingTitle = title ?: "Et ilanı",
    partyName = slaughterhouseName ?: "Kesimhane",
    partyCompany = null,
    partyInitials = initialsFrom(slaughterhouseName),
    offerAmount = pricePerKg,
    originalPrice = null,
    quantityLabel = quantity?.let { "$it kg" },
    status = status,
    dateLabel = createdAt,
    city = null,
    index = index,
)
