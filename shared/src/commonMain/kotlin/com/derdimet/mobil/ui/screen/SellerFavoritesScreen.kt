package com.derdimet.mobil.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.CreateAnimalOfferPayload
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimAnimalPurchaseCard
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import kotlinx.coroutines.launch

@Composable
fun SellerFavoritesScreen(
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var requests by remember { mutableStateOf<List<AnimalPurchaseRequestDto>>(emptyList()) }
    var favoriteRequestIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var favSubmittingId by remember { mutableStateOf<Int?>(null) }
    var favToggleNonce by remember { mutableIntStateOf(0) }
    var favoriteError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var detailRequest by remember { mutableStateOf<AnimalPurchaseRequestDto?>(null) }
    var offerForRequest by remember { mutableStateOf<AnimalPurchaseRequestDto?>(null) }

    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.fetchFavoriteAnimalPurchaseRequests()
        if (res.success) {
            val data = res.data ?: emptyList()
            requests = data
            favoriteRequestIds = data.map { it.id }.toSet()
        } else {
            error = res.message ?: "Favoriler alınamadı"
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    LaunchedEffect(favSubmittingId, favToggleNonce) {
        val requestId = favSubmittingId ?: return@LaunchedEffect
        favoriteError = null
        try {
            val res = marketService.toggleAnimalPurchaseRequestFavorite(requestId.toLong())
            if (res.success) {
                val nowFav = res.data?.isFavoritedByMe == true
                favoriteRequestIds = if (nowFav) favoriteRequestIds + requestId else favoriteRequestIds - requestId
                if (nowFav) {
                    requests = requests.map { if (it.id == requestId) it.copy(isFavoritedByMe = true) else it }
                } else {
                    requests = requests.filter { it.id != requestId }
                    detailRequest = detailRequest?.takeIf { it.id != requestId }
                }
                detailRequest = detailRequest?.takeIf { it.id == requestId }?.copy(isFavoritedByMe = nowFav) ?: detailRequest
            } else {
                favoriteError = res.message ?: "Favori işlemi başarısız"
            }
        } finally {
            if (favSubmittingId == requestId) favSubmittingId = null
        }
    }

    val offerReq = offerForRequest
    if (offerReq != null) {
        OfferCreateScreen(
            title = offerReq.title,
            subtitle = "Kesimhane: ${offerReq.slaughterhouseCompanyName ?: offerReq.slaughterhouseName ?: "-"}",
            contextLine = "Kategori: ${offerReq.animalCategory ?: "-"} • İhtiyaç: ${offerReq.quantity ?: "-"}",
            showQuantityAsInt = true,
            quantityLabel = "Adet",
            onBack = { offerForRequest = null },
            onSuccess = {
                offerForRequest = null
                detailRequest = null
            },
            submit = { price, qty, note ->
                val res = marketService.createAnimalOffer(
                    requestId = offerReq.id,
                    payload = CreateAnimalOfferPayload(
                        pricePerKg = price,
                        animalCount = qty.toInt(),
                        note = note,
                    ),
                )
                Pair(res.success, res.message)
            },
        )
        return
    }

    val detailItem = detailRequest
    if (detailItem != null) {
        AnimalPurchaseRequestDetailScreen(
            requestId = detailItem.id.toLong(),
            initialRequest = detailItem,
            isFavorited = favoriteRequestIds.contains(detailItem.id),
            favoriteError = favoriteError,
            onFavoriteToggle = {
                favSubmittingId = detailItem.id
                favToggleNonce++
            },
            marketService = marketService,
            onBack = { detailRequest = null },
            onMakeOffer = { item -> offerForRequest = item },
            onMessage = { detailRequest = null },
            onOpenSlaughterhouseProfile = {},
        )
        return
    }

    Column(Modifier.fillMaxSize().background(FigmaStyle.ScreenBg)) {
        DerdimTopBar(title = "Favorilerim", showBack = true, onBack = onBack)
        favoriteError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
        DerdimListScreenBody(
            header = {
                Text(
                    "${requests.size} favori talep",
                    fontSize = 12.sp,
                    color = DerdimColors.MutedForeground,
                )
            },
            content = {
                DerdimScreenState(
                    loading = isLoading,
                    error = error,
                    empty = requests.isEmpty(),
                    emptyTitle = "Henüz favori talebiniz yok",
                    emptyMessage = "Arama ekranından kesimhane taleplerini favorileyebilirsiniz.",
                    onRetry = { scope.launch { refresh() } },
                ) {
                    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(requests.size, key = { requests[it].id }) { index ->
                            val item = requests[index]
                            DerdimAnimalPurchaseCard(
                                item = item,
                                index = index,
                                isFavorited = favoriteRequestIds.contains(item.id),
                                onFavoriteClick = {
                                    favSubmittingId = item.id
                                    favToggleNonce++
                                },
                                onClick = { detailRequest = item.copy(isFavoritedByMe = true) },
                                onOfferClick = { offerForRequest = item },
                            )
                        }
                    }
                }
            },
        )
    }
}
