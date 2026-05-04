package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.*
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.derdimet.mobil.viewmodel.ViewModel

class SellerViewModel(private val marketService: MarketService) : ViewModel() {
    private val _requests = MutableStateFlow<List<AnimalPurchaseRequestDto>>(emptyList())
    val requests: StateFlow<List<AnimalPurchaseRequestDto>> = _requests.asStateFlow()

    private val _offers = MutableStateFlow<List<SellerAnimalOfferItemDto>>(emptyList())
    val offers: StateFlow<List<SellerAnimalOfferItemDto>> = _offers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reqs = marketService.fetchOpenAnimalPurchaseRequests()
                val offs = marketService.fetchMyAnimalOffers()
                if (reqs.success) _requests.value = reqs.data
                if (offs.success) _offers.value = offs.data
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitOffer(requestId: Int, price: Double, count: Int?, note: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val response = marketService.createAnimalOffer(requestId, CreateAnimalOfferPayload(price, count, note))
            if (response.success) {
                loadData()
                onSuccess()
            } else {
                _error.value = response.message
            }
        }
    }
}
