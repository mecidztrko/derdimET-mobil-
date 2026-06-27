package com.derdimet.mobil.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListUiState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOfflineCache: Boolean = false,
)

abstract class CachedListViewModel<T> : ViewModel() {

    private val _state = MutableStateFlow(ListUiState<T>())
    val state: StateFlow<ListUiState<T>> = _state.asStateFlow()

    protected abstract suspend fun fetchFromNetwork(): NetworkListResult<T>
    protected abstract fun loadCache(): List<T>
    protected abstract fun saveCache(items: List<T>)
    protected abstract fun defaultErrorMessage(): String

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isOfflineCache = false) }
            when (val result = fetchFromNetwork()) {
                is NetworkListResult.Success -> {
                    saveCache(result.items)
                    _state.update {
                        it.copy(items = result.items, isLoading = false, error = null, isOfflineCache = false)
                    }
                }
                is NetworkListResult.Failure -> {
                    val cached = loadCache()
                    if (cached.isNotEmpty()) {
                        _state.update {
                            it.copy(
                                items = cached,
                                isLoading = false,
                                error = result.message ?: defaultErrorMessage(),
                                isOfflineCache = true,
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(isLoading = false, error = result.message ?: defaultErrorMessage())
                        }
                    }
                }
            }
        }
    }

    fun updateItems(transform: (List<T>) -> List<T>) {
        _state.update { it.copy(items = transform(it.items)) }
    }
}

sealed interface NetworkListResult<out T> {
    data class Success<T>(val items: List<T>) : NetworkListResult<T>
    data class Failure(val message: String?) : NetworkListResult<Nothing>
}
