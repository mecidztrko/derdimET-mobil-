package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.SellerAnimalListingDto
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SlaughterhouseSearchFilters(
    val sort: String = "newest",
    val category: AnimalCategory? = null,
    val type: String = "",
    val ageMin: String = "",
    val ageMax: String = "",
    val quantityMin: String = "",
    val quantityMax: String = "",
    val priceMin: String = "",
    val priceMax: String = "",
)

class SlaughterhouseSearchViewModel(
    private val marketService: MarketService,
    private val listingCacheRepository: ListingCacheRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ListUiState<SellerAnimalListingDto>())
    val state = _state.asStateFlow()

    private var favoriteListingIds = mutableSetOf<Long>()
    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteIds = _favoriteIds.asStateFlow()

    init {
        load(SlaughterhouseSearchFilters())
    }

    fun load(filters: SlaughterhouseSearchFilters) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isOfflineCache = false) }
            val res = marketService.searchSlaughterhouseAnimalListingsFiltered(
                category = filters.category?.name,
                type = filters.type.takeIf { it.isNotBlank() },
                ageMin = parseInt(filters.ageMin),
                ageMax = parseInt(filters.ageMax),
                quantityMin = parseInt(filters.quantityMin),
                quantityMax = parseInt(filters.quantityMax),
                priceMin = parseDouble(filters.priceMin),
                priceMax = parseDouble(filters.priceMax),
                sort = filters.sort,
            )
            if (res.success) {
                val data = res.data.orEmpty()
                listingCacheRepository.saveAnimalListings(data)
                favoriteListingIds = data.filter { it.isFavoritedByMe == true }.map { it.id }.toMutableSet()
                _favoriteIds.value = favoriteListingIds.toSet()
                _state.update { it.copy(items = data, isLoading = false, error = null, isOfflineCache = false) }
            } else {
                val cached = listingCacheRepository.loadAnimalListings()
                if (cached.isNotEmpty()) {
                    favoriteListingIds = cached.filter { it.isFavoritedByMe == true }.map { it.id }.toMutableSet()
                    _favoriteIds.value = favoriteListingIds.toSet()
                    _state.update {
                        it.copy(
                            items = cached,
                            isLoading = false,
                            error = res.message ?: "Liste alınamadı",
                            isOfflineCache = true,
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = res.message ?: "Liste alınamadı") }
                }
            }
        }
    }

    fun applyFavoriteToggle(listingId: Long, isFavorited: Boolean) {
        if (isFavorited) favoriteListingIds.add(listingId) else favoriteListingIds.remove(listingId)
        _favoriteIds.value = favoriteListingIds.toSet()
        _state.update { s ->
            s.copy(items = s.items.map { if (it.id == listingId) it.copy(isFavoritedByMe = isFavorited) else it })
        }
    }

    private fun parseInt(s: String): Int? = s.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
    private fun parseDouble(s: String): Double? =
        s.trim().takeIf { it.isNotEmpty() }?.replace(',', '.')?.toDoubleOrNull()
}
