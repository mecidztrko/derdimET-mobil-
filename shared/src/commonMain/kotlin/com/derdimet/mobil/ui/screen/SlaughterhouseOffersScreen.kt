package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.model.OfferEventDto
import com.derdimet.mobil.model.OfferStatus
import com.derdimet.mobil.model.ReviseOfferPayload
import com.derdimet.mobil.model.SlaughterhouseIncomingMeatOfferDto
import com.derdimet.mobil.model.SlaughterhouseListingOfferDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimActionBadge
import com.derdimet.mobil.ui.components.DerdimFilterTabs
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimOfferCard
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimStatsRow
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.components.OfferCardData
import com.derdimet.mobil.ui.components.OfferHistorySheet
import com.derdimet.mobil.ui.components.OfferReviseSheet
import com.derdimet.mobil.ui.components.initialsFrom
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlin.math.abs

@Composable
fun SlaughterhouseOffersScreen(marketService: MarketService) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var incoming by remember { mutableStateOf<List<SlaughterhouseIncomingMeatOfferDto>>(emptyList()) }
    var sent by remember { mutableStateOf<List<SlaughterhouseListingOfferDto>>(emptyList()) }
    var section by remember { mutableStateOf("incoming") }
    var statusFilter by remember { mutableStateOf("") }
    var refreshKey by remember { mutableIntStateOf(0) }
    var actingOfferId by remember { mutableStateOf<Long?>(null) }
    var actingOfferNonce by remember { mutableIntStateOf(0) }
    var selectedConversation by remember { mutableStateOf<ConversationItemDto?>(null) }
    var chatTargetUserId by remember { mutableStateOf<Long?>(null) }
    var chatLaunchNonce by remember { mutableIntStateOf(0) }
    var reviseOffer by remember { mutableStateOf<SlaughterhouseListingOfferDto?>(null) }
    var historyOfferId by remember { mutableStateOf<Long?>(null) }
    var historyEvents by remember { mutableStateOf<List<OfferEventDto>>(emptyList()) }
    var historyLoading by remember { mutableStateOf(false) }
    var historyError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(historyOfferId) {
        val id = historyOfferId ?: return@LaunchedEffect
        historyLoading = true
        historyError = null
        val res = marketService.fetchSlaughterhouseListingOfferHistory(id)
        if (res.success) historyEvents = res.data.orEmpty() else historyError = res.message ?: "Geçmiş alınamadı"
        historyLoading = false
    }

    val reviseTarget = reviseOffer
    if (reviseTarget != null) {
        OfferReviseSheet(
            title = "${reviseTarget.listingType ?: "Hayvan"} ilanı",
            quantityLabel = "Adet",
            showQuantityAsInt = true,
            initialPrice = reviseTarget.pricePerKg,
            initialQuantity = reviseTarget.quantity?.toDouble(),
            initialNote = reviseTarget.note,
            onDismiss = { reviseOffer = null },
            onSubmit = { price, qty, note ->
                val res = marketService.reviseSlaughterhouseListingOffer(
                    reviseTarget.offerId,
                    ReviseOfferPayload(pricePerKg = price, quantity = qty, note = note),
                )
                if (res.success) {
                    reviseOffer = null
                    refreshKey++
                    Pair(true, null)
                } else {
                    Pair(false, res.message)
                }
            },
        )
    }

    if (historyOfferId != null) {
        OfferHistorySheet(
            events = historyEvents,
            loading = historyLoading,
            error = historyError,
            onDismiss = {
                historyOfferId = null
                historyEvents = emptyList()
            },
        )
    }

    LaunchedEffect(chatTargetUserId, chatLaunchNonce) {
        val otherId = chatTargetUserId ?: return@LaunchedEffect
        try {
            val res = marketService.getOrCreateConversation(otherId)
            if (res.success && res.data != null) {
                selectedConversation = res.data
            } else {
                error = res.message ?: "Sohbet başlatılamadı"
            }
        } finally {
            if (chatTargetUserId == otherId) chatTargetUserId = null
        }
    }

    LaunchedEffect(actingOfferId, actingOfferNonce) {
        val id = actingOfferId ?: return@LaunchedEffect
        try {
            val accept = id > 0
            val offerId = abs(id)
            val res = if (accept) marketService.acceptSlaughterhouseMeatOffer(offerId)
            else marketService.rejectSlaughterhouseMeatOffer(offerId)
            if (!res.success) error = res.message ?: "İşlem başarısız"
            else refreshKey++
        } finally {
            if (actingOfferId == id) actingOfferId = null
        }
    }

    val chatConvo = selectedConversation
    if (chatConvo != null) {
        ChatScreen(
            marketService = marketService,
            conversationId = chatConvo.conversationId,
            title = chatConvo.otherUserName ?: (chatConvo.otherUserEmail ?: "Sohbet"),
            onBack = { selectedConversation = null },
        )
        return
    }

    LaunchedEffect(refreshKey) {
        isLoading = true
        error = null
        val resIncoming = marketService.fetchSlaughterhouseIncomingMeatOffers()
        val resSent = marketService.fetchMySlaughterhouseListingOffers()
        if (resIncoming.success) incoming = resIncoming.data ?: emptyList()
        else error = resIncoming.message ?: "Gelen teklifler alınamadı"
        if (resSent.success) sent = resSent.data ?: emptyList()
        else if (error == null) error = resSent.message ?: "Gönderilen teklifler alınamadı"
        isLoading = false
    }

    val activeStatuses = if (section == "incoming") incoming.map { it.status } else sent.map { it.status }
    val pending = activeStatuses.count { it == OfferStatus.PENDING }
    val accepted = activeStatuses.count { it == OfferStatus.ACCEPTED }
    val rejected = activeStatuses.count { it == OfferStatus.REJECTED }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(
            title = "Teklifler",
            action = { if (pending > 0) DerdimActionBadge("$pending bekliyor") },
        )
        DerdimListScreenBody(
            header = {
                DerdimFilterTabs(
                    tabs = listOf(
                        Triple("incoming", "Gelen", incoming.size),
                        Triple("sent", "Gönderilen", sent.size),
                    ),
                    selectedKey = section,
                    onSelect = {
                        section = it
                        statusFilter = ""
                    },
                )
                DerdimStatsRow(pending, accepted, rejected)
                DerdimFilterTabs(
                    tabs = listOf(
                        Triple("", "Tümü", activeStatuses.size),
                        Triple("pending", "Bekleyen", pending),
                        Triple("accepted", "Kabul", accepted),
                        Triple("rejected", "Reddedilen", rejected),
                    ),
                    selectedKey = statusFilter,
                    onSelect = { statusFilter = it },
                )
            },
            content = {
                DerdimScreenState(
                    loading = isLoading,
                    error = error,
                    empty = false,
                    onRetry = { refreshKey++ },
                ) {
                when {
                    section == "incoming" -> {
                        val list = incoming.filterMeatOffersByStatus(statusFilter)
                        if (list.isEmpty()) {
                            Text("Bu filtrede teklif yok.", color = DerdimColors.MutedForeground)
                        } else {
                            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                itemsIndexed(list, key = { _, it -> it.offerId }) { index, item ->
                                    DerdimOfferCard(
                                        offer = item.toOfferCardData(index),
                                        showActions = true,
                                        onAccept = if (item.status == OfferStatus.PENDING) {
                                            {
                                                actingOfferId = item.offerId
                                                actingOfferNonce++
                                            }
                                        } else null,
                                        onReject = if (item.status == OfferStatus.PENDING) {
                                            {
                                                actingOfferId = -item.offerId
                                                actingOfferNonce++
                                            }
                                        } else null,
                                        onMessage = item.buyerId?.let { uid ->
                                            {
                                                chatTargetUserId = uid
                                                chatLaunchNonce++
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        val list = sent.filterListingOffersByStatus(statusFilter)
                        if (list.isEmpty()) {
                            Text("Bu filtrede teklif yok.", color = DerdimColors.MutedForeground)
                        } else {
                            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                itemsIndexed(list, key = { _, it -> it.offerId }) { index, item ->
                                    DerdimOfferCard(
                                        offer = item.toOfferCardData(index),
                                        showActions = true,
                                        onMessage = item.sellerId?.let { uid ->
                                            {
                                                chatTargetUserId = uid
                                                chatLaunchNonce++
                                            }
                                        },
                                        onRevise = if (item.status == OfferStatus.PENDING) {
                                            { reviseOffer = item }
                                        } else null,
                                        onHistory = { historyOfferId = item.offerId },
                                    )
                                }
                            }
                        }
                    }
                }
                }
            },
        )
    }

}

private fun List<SlaughterhouseIncomingMeatOfferDto>.filterMeatOffersByStatus(filter: String) = when (filter) {
    "pending" -> filter { it.status == OfferStatus.PENDING }
    "accepted" -> filter { it.status == OfferStatus.ACCEPTED }
    "rejected" -> filter { it.status == OfferStatus.REJECTED }
    else -> this
}

private fun List<SlaughterhouseListingOfferDto>.filterListingOffersByStatus(filter: String) = when (filter) {
    "pending" -> filter { it.status == OfferStatus.PENDING }
    "accepted" -> filter { it.status == OfferStatus.ACCEPTED }
    "rejected" -> filter { it.status == OfferStatus.REJECTED }
    else -> this
}

private fun SlaughterhouseIncomingMeatOfferDto.toOfferCardData(index: Int) = OfferCardData(
    id = offerId,
    listingTitle = saleRequestTitle ?: "Et ilanı",
    partyName = buyerName ?: "Alıcı",
    partyCompany = null,
    partyInitials = initialsFrom(buyerName),
    offerAmount = pricePerKg,
    originalPrice = null,
    quantityLabel = quantity?.let { "$it kg" },
    status = status,
    dateLabel = createdAt.take(10),
    city = null,
    index = index,
)

private fun SlaughterhouseListingOfferDto.toOfferCardData(index: Int): OfferCardData {
    val (revisionLabel, expiryLabel) = offerMetaLabels(revisionNumber, expiresAt)
    return OfferCardData(
        id = offerId,
        listingTitle = "${listingType ?: "Hayvan"} · ${listingCategory ?: ""}".trim(),
        partyName = sellerName ?: "Satıcı",
        partyCompany = null,
        partyInitials = initialsFrom(sellerName),
        offerAmount = pricePerKg,
        originalPrice = null,
        quantityLabel = quantity?.let { "$it adet" },
        status = status,
        dateLabel = createdAt.take(10),
        city = null,
        revisionLabel = revisionLabel,
        expiryLabel = expiryLabel,
        index = index,
    )
}
