package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.AnimalPurchaseRequestDto
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SellerSearchFilters(
    val sort: String = "newest",
    val category: AnimalCategory? = null,
    val quantityMin: String = "",
    val quantityMax: String = "",
    val weightMin: String = "",
    val weightMax: String = "",
)

data class SellerSearchUiState(
    val myListings: List<SellerAnimalListingDto> = emptyList(),
    val requests: List<AnimalPurchaseRequestDto> = emptyList(),
    val favoriteRequestIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOfflineCache: Boolean = false,
)

class SellerSearchViewModel(
    private val marketService: MarketService,
    private val listingCacheRepository: ListingCacheRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SellerSearchUiState())
    val state: StateFlow<SellerSearchUiState> = _state.asStateFlow()

    fun load(tab: String, query: String, filters: SellerSearchFilters) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isOfflineCache = false) }
            val q = query.takeIf { it.isNotBlank() }
            var error: String? = null
            var offline = false

            val myRes = marketService.fetchMySellerAnimalListings(q = q)
            val myListings = if (myRes.success) {
                val data = myRes.data.orEmpty()
                listingCacheRepository.saveMySellerListings(data)
                data
            } else if (tab == "my_listings") {
                val cached = listingCacheRepository.loadMySellerListings()
                if (cached.isNotEmpty()) {
                    offline = true
                    error = myRes.message ?: "İlanlarınız alınamadı"
                    cached
                } else {
                    error = myRes.message ?: "İlanlarınız alınamadı"
                    emptyList()
                }
            } else {
                _state.value.myListings
            }

            var requests = _state.value.requests
            var favoriteRequestIds = _state.value.favoriteRequestIds

            if (tab == "requests") {
                val res = marketService.fetchOpenAnimalPurchaseRequestsFiltered(
                    category = filters.category?.name,
                    q = q,
                    sort = filters.sort,
                    quantityMin = parseInt(filters.quantityMin),
                    quantityMax = parseInt(filters.quantityMax),
                    expectedWeightMin = parseDouble(filters.weightMin),
                    expectedWeightMax = parseDouble(filters.weightMax),
                )
                if (res.success) {
                    val data = res.data.orEmpty()
                    listingCacheRepository.savePurchaseRequests(data)
                    requests = data
                    favoriteRequestIds = data.filter { it.isFavoritedByMe == true }.map { it.id }.toSet()
                } else {
                    val cached = listingCacheRepository.loadPurchaseRequests()
                    if (cached.isNotEmpty()) {
                        offline = true
                        error = res.message ?: "Talepler alınamadı"
                        requests = cached
                        favoriteRequestIds = cached.filter { it.isFavoritedByMe == true }.map { it.id }.toSet()
                    } else {
                        error = res.message ?: "Talepler alınamadı"
                        requests = emptyList()
                        favoriteRequestIds = emptySet()
                    }
                }
            }

            _state.update {
                it.copy(
                    myListings = myListings,
                    requests = requests,
                    favoriteRequestIds = favoriteRequestIds,
                    isLoading = false,
                    error = error,
                    isOfflineCache = offline,
                )
            }
        }
    }

    fun applyFavoriteToggle(requestId: Int, isFavorited: Boolean) {
        _state.update { state ->
            val ids = if (isFavorited) state.favoriteRequestIds + requestId else state.favoriteRequestIds - requestId
            state.copy(
                favoriteRequestIds = ids,
                requests = state.requests.map { if (it.id == requestId) it.copy(isFavoritedByMe = isFavorited) else it },
            )
        }
    }

    private fun parseInt(value: String): Int? = value.trim().toIntOrNull()

    private fun parseDouble(value: String): Double? = value.trim().replace(',', '.').toDoubleOrNull()
}
