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
import com.derdimet.mobil.model.CreateSlaughterhouseListingOfferPayload
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.service.MarketService
import com.derdimet.mobil.ui.components.DerdimAnimalListingCard
import com.derdimet.mobil.ui.components.DerdimListScreenBody
import com.derdimet.mobil.ui.components.DerdimScreenState
import com.derdimet.mobil.ui.components.DerdimTopBar
import com.derdimet.mobil.ui.components.FigmaStyle
import com.derdimet.mobil.ui.theme.DerdimColors
import com.derdimet.mobil.util.toggleFavoriteIdSet
import kotlinx.coroutines.launch

@Composable
fun SlaughterhouseFavoritesScreen(
    marketService: MarketService,
    onBack: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var listings by remember { mutableStateOf<List<SellerAnimalListingDto>>(emptyList()) }
    var favoriteListingIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var favSubmittingId by remember { mutableStateOf<Long?>(null) }
    var favToggleNonce by remember { mutableIntStateOf(0) }
    var favoriteError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var detailListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }
    var offerForListing by remember { mutableStateOf<SellerAnimalListingDto?>(null) }

    suspend fun refresh() {
        isLoading = true
        error = null
        val res = marketService.fetchFavoriteAnimalListings()
        if (res.success) {
            val data = res.data ?: emptyList()
            listings = data
            favoriteListingIds = data.map { it.id }.toSet()
        } else {
            error = res.message ?: "Favoriler alınamadı"
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    LaunchedEffect(favSubmittingId, favToggleNonce) {
        val listingId = favSubmittingId ?: return@LaunchedEffect
        favoriteError = null
        try {
            val res = marketService.toggleAnimalListingFavorite(listingId)
            if (res.success) {
                val nowFav = res.data?.isFavoritedByMe == true
                favoriteListingIds = toggleFavoriteIdSet(favoriteListingIds, listingId, nowFav)
                if (nowFav) {
                    listings = listings.map { if (it.id == listingId) it.copy(isFavoritedByMe = true) else it }
                } else {
                    listings = listings.filter { it.id != listingId }
                    detailListing = detailListing?.takeIf { it.id != listingId }
                }
                detailListing = detailListing?.takeIf { it.id == listingId }?.copy(isFavoritedByMe = nowFav) ?: detailListing
            } else {
                favoriteError = res.message ?: "Favori işlemi başarısız"
            }
        } finally {
            if (favSubmittingId == listingId) favSubmittingId = null
        }
    }

    val offerListing = offerForListing
    if (offerListing != null) {
        OfferCreateScreen(
            title = offerListing.type,
            subtitle = "Satıcı: ${offerListing.sellerName ?: "-"}",
            contextLine = "Adet: ${offerListing.quantity} • Fiyat: ${offerListing.price ?: "-"}",
            showQuantityAsInt = true,
            quantityLabel = "Adet",
            onBack = { offerForListing = null },
            onSuccess = {
                offerForListing = null
                detailListing = null
            },
            submit = { price, qty, note ->
                val res = marketService.createSlaughterhouseListingOffer(
                    listingId = offerListing.id,
                    payload = CreateSlaughterhouseListingOfferPayload(
                        pricePerKg = price,
                        quantity = qty.toInt(),
                        note = note,
                    ),
                )
                Pair(res.success, res.message)
            },
        )
        return
    }

    val detailItem = detailListing
    if (detailItem != null) {
        SellerAnimalListingDetailScreen(
            listingId = detailItem.id,
            initialListing = detailItem,
            isFavorited = favoriteListingIds.contains(detailItem.id),
            favoriteError = favoriteError,
            onFavoriteToggle = {
                favSubmittingId = detailItem.id
                favToggleNonce++
            },
            marketService = marketService,
            onBack = { detailListing = null },
            onMakeOffer = { item -> offerForListing = item },
            onMessage = { detailListing = null },
            onOpenSellerProfile = {},
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
                    "${listings.size} favori ilan",
                    fontSize = 12.sp,
                    color = DerdimColors.MutedForeground,
                )
            },
            content = {
                DerdimScreenState(
                    loading = isLoading,
                    error = error,
                    empty = listings.isEmpty(),
                    emptyTitle = "Henüz favori ilanınız yok",
                    emptyMessage = "Arama ekranından hayvan ilanlarını favorileyebilirsiniz.",
                    onRetry = { scope.launch { refresh() } },
                ) {
                    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listings.size, key = { listings[it].id }) { index ->
                            val item = listings[index]
                            DerdimAnimalListingCard(
                                item = item,
                                index = index,
                                isFavorited = favoriteListingIds.contains(item.id),
                                onFavoriteClick = {
                                    favSubmittingId = item.id
                                    favToggleNonce++
                                },
                                onClick = { detailListing = item.copy(isFavoritedByMe = true) },
                                onOfferClick = { offerForListing = item },
                            )
                        }
                    }
                }
            },
        )
    }
}
