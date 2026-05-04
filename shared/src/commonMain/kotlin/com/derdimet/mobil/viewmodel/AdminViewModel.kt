package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.AnimalCategory
import com.derdimet.mobil.model.CreateAnimalPurchasePayload
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.derdimet.mobil.viewmodel.ViewModel

class AdminViewModel(private val marketService: MarketService) : ViewModel() {
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _animalCategory = MutableStateFlow<AnimalCategory?>(null)
    val animalCategory: StateFlow<AnimalCategory?> = _animalCategory.asStateFlow()

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity.asStateFlow()

    private val _expectedWeight = MutableStateFlow("")
    val expectedWeight: StateFlow<String> = _expectedWeight.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onTitleChange(value: String) { _title.value = value }
    fun onCategoryChange(value: AnimalCategory) { _animalCategory.value = value }
    fun onQuantityChange(value: String) { _quantity.value = value }
    fun onWeightChange(value: String) { _expectedWeight.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }

    fun submitRequest() {
        if (_title.value.isEmpty()) {
            _error.value = "Başlık zorunlu"
            return
        }
        if (_animalCategory.value == null) {
            _error.value = "Hayvan türü seçin"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _message.value = null
            try {
                val q = _quantity.value.toIntOrNull()
                val w = _expectedWeight.value.toDoubleOrNull()
                
                val response = marketService.createAnimalPurchaseRequest(
                    CreateAnimalPurchasePayload(
                        title = _title.value,
                        animalCategory = _animalCategory.value!!,
                        quantity = q,
                        expectedWeight = w,
                        description = _description.value.takeIf { it.isNotEmpty() }
                    )
                )
                if (response.success) {
                    _message.value = "İlan oluşturuldu"
                    resetForm()
                } else {
                    _error.value = response.message ?: "Bir hata oluştu"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun resetForm() {
        _title.value = ""
        _animalCategory.value = null
        _quantity.value = ""
        _expectedWeight.value = ""
        _description.value = ""
    }
}
