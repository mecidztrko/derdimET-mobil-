package com.derdimet.mobil.viewmodel

import com.derdimet.mobil.model.ConversationItemDto
import com.derdimet.mobil.repository.ListingCacheRepository
import com.derdimet.mobil.service.MarketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessagesInboxUiState(
    val conversations: List<ConversationItemDto> = emptyList(),
    val lastMessagePreview: Map<Long, String> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOfflineCache: Boolean = false,
)

class MessagesInboxViewModel(
    private val marketService: MarketService,
    private val listingCacheRepository: ListingCacheRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MessagesInboxUiState())
    val state: StateFlow<MessagesInboxUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isOfflineCache = false) }
            val res = marketService.fetchConversations()
            if (res.success) {
                val conversations = res.data.orEmpty()
                listingCacheRepository.saveConversations(conversations)
                val previews = mutableMapOf<Long, String>()
                conversations.take(15).forEach { convo ->
                    val msgRes = marketService.fetchMessages(convo.conversationId)
                    if (msgRes.success) {
                        msgRes.data?.lastOrNull()?.text?.let { previews[convo.conversationId] = it }
                    }
                }
                _state.update {
                    it.copy(
                        conversations = conversations,
                        lastMessagePreview = previews,
                        isLoading = false,
                        error = null,
                        isOfflineCache = false,
                    )
                }
            } else {
                val cached = listingCacheRepository.loadConversations()
                if (cached.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            conversations = cached,
                            isLoading = false,
                            error = res.message ?: "Mesajlar alınamadı",
                            isOfflineCache = true,
                        )
                    }
                } else {
                    _state.update {
                        it.copy(isLoading = false, error = res.message ?: "Mesajlar alınamadı")
                    }
                }
            }
        }
    }
}
