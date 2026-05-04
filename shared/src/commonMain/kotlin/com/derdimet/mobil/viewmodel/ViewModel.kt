package com.derdimet.mobil.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Platform-agnostic ViewModel base class.
 * Replaces moe.tlaster.precompose.viewmodel.ViewModel which has no wasmJs support.
 */
open class ViewModel {
    val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    open fun onCleared() {
        viewModelScope.cancel()
    }
}
