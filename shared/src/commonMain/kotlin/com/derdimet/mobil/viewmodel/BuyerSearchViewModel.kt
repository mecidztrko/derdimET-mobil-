package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.MeatSaleRequestDto
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuyerSearchUiState(
    val listings: List<MeatSaleRequestDto> = emptyList(),
    val favoriteListingIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOfflineCache: Boolean = false,
)

class BuyerSearchViewModel(
    private val marketService: MarketService,
    private val listingCacheRepository: ListingCacheRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuyerSearchUiState())
    val uiState: StateFlow<BuyerSearchUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isOfflineCache = false) }
            val res = marketService.fetchOpenMeatSaleRequests()
            if (res.success) {
                val data = res.data.orEmpty()
                listingCacheRepository.saveMeatListings(data)
                _uiState.update {
                    it.copy(
                        listings = data,
                        favoriteListingIds = data.filter { item -> item.isFavoritedByMe == true }.map { item -> item.id }.toSet(),
                        isLoading = false,
                        error = null,
                        isOfflineCache = false,
                    )
                }
            } else {
                val cached = listingCacheRepository.loadMeatListings()
                if (cached.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            listings = cached,
                            favoriteListingIds = cached.filter { item -> item.isFavoritedByMe == true }.map { item -> item.id }.toSet(),
                            isLoading = false,
                            error = res.message ?: "Liste alınamadı",
                            isOfflineCache = true,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = res.message ?: "Liste alınamadı")
                    }
                }
            }
        }
    }

    fun applyFavoriteToggle(listingId: Long, isFavorited: Boolean) {
        _uiState.update { state ->
            val ids = if (isFavorited) state.favoriteListingIds + listingId else state.favoriteListingIds - listingId
            state.copy(
                favoriteListingIds = ids,
                listings = state.listings.map { if (it.id == listingId) it.copy(isFavoritedByMe = isFavorited) else it },
            )
        }
    }
}
