package org.beem.tastymap.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.ai.AiStreamEvent
import org.beem.tastymap.data.model.ai.RecommendationReq
import org.beem.tastymap.data.repository.AiChatRepository
import org.beem.tastymap.ui.ai.model.AiSessionRef
import org.beem.tastymap.ui.ai.model.ChatMessage
import org.beem.tastymap.ui.ai.model.toUiState
import org.beem.tastymap.ui.ai.model.updateFromStream
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AiViewModel(
    private val aiChatRepository: AiChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state: StateFlow<AiChatState> = _state.asStateFlow()

    init {
        observeSidebarSessions()
        observeActiveSessionMessages()

        if (aiChatRepository.sessionSummaries.value.isEmpty()) {
            loadSessions(page = 0)
        }
        loadQuotaStatus()
    }

    fun onIntent(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.InputTextChanged -> {
                _state.update { it.copy(inputText = intent.text) }
            }

            is AiChatIntent.SendMessage -> {
                handleSendMessage()
            }

            is AiChatIntent.NewChat -> {
                handleNewChat()
            }

            is AiChatIntent.SelectSession -> {
                loadSessionMessages(intent.sessionId)
            }

            is AiChatIntent.DeleteSession -> {
                handleDeleteSession(intent.sessionId)
            }

            is AiChatIntent.LoadMoreSessions -> {
                handleLoadMoreSessions()
            }

            is AiChatIntent.ToggleSidebar -> {
                _state.update { it.copy(isSidebarOpen = !it.isSidebarOpen) }
            }

            is AiChatIntent.DismissError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            is AiChatIntent.NavigateBack -> {
                // Navigasyon katmanına delegate edilebilir
            }

            is AiChatIntent.PlaceClicked -> {

            }
            is AiChatIntent.ShowPlacePreview -> _state.update { it.copy(previewPlace = intent.place) }
            is AiChatIntent.DismissPlacePreview -> _state.update { it.copy(previewPlace = null) }
            is AiChatIntent.DirectionsClicked -> {

            }
            is AiChatIntent.ShowOnMapClicked -> {

            }
            is AiChatIntent.SelectProvider -> {
                _state.update { it.copy(selectedProvider = intent.provider) }
            }
            is AiChatIntent.ShowUpgradeSheet -> {
                _state.update { it.copy(isUpgradeSheetVisible = true) }
            }
            is AiChatIntent.DismissUpgradeSheet -> {
                _state.update { it.copy(isUpgradeSheetVisible = false) }
            }

            AiChatIntent.RetryLastMessage -> handleRetryLastMessage()
            AiChatIntent.RetryLoadSessions -> handleRetryLoadSessionAndQuota()
            AiChatIntent.RetryLoadMessages -> handleRetryMessages()
            AiChatIntent.LoadOlderMessages -> loadOlderMessages()
        }
    }

    private fun observeSidebarSessions() {
        viewModelScope.launch {
            aiChatRepository.sessionSummaries.collect { cachedList ->
                _state.update { it.copy(sessions = cachedList) }
            }
        }
    }

    private fun observeActiveSessionMessages() {
        viewModelScope.launch {
            _state
                .map { it.sessionRef?.cacheKey }
                .distinctUntilChanged()
                .flatMapLatest { sessionKey ->
                    if (sessionKey == null) flowOf(null)
                    else aiChatRepository.observeSession(sessionKey)
                }
                .collect { cachedSession ->
                    _state.update { current ->
                        current.copy(
                            messages = cachedSession?.messages.orEmpty(),
                            isStreaming = cachedSession?.isStreaming ?: false
                        )
                    }
                }
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    private fun handleSendMessage() {
        val currentState = _state.value
        val query = _state.value.inputText.trim()
        if (query.isBlank() || _state.value.isStreaming) return

        val targetRef = currentState.sessionRef
            ?: AiSessionRef.Pending("pending_${Clock.System.now().toEpochMilliseconds()}")

        val request = RecommendationReq(
            sessionId = targetRef.remoteId,
            clientMessageId = Uuid.random().toString(),
            query = query,
            latitude = currentState.userLatitude,
            longitude = currentState.userLongitude,
            radiusKm = 5.0,
            ignoreHealthProfile = false,
            provider = currentState.selectedProvider.name
        )

        _state.update { current ->
            current.copy(
                sessionRef = targetRef,
                inputText = "",
                isStreaming = true,
                previewPlace = null,
                errorMessage = null
            )
        }

        aiChatRepository.sendMessageInBackground(
            initialRef = targetRef,
            request = request,
            onSessionPromoted = { pendingKey, activeRef ->
                _state.update { latest ->
                    if (latest.sessionRef?.cacheKey == pendingKey) {
                        latest.copy(sessionRef = activeRef)
                    } else latest
                }
            },
            onQuotaUpdated = { quotaEvent ->
                _state.update { it.copy(quotaState = it.quotaState.updateFromStream(quotaEvent.quota)) }
            },
            onErrorMessage = { sessionKey, err ->
                _state.update { latest ->
                    if (latest.sessionRef?.cacheKey == sessionKey) {
                        latest.copy(
                            errorMessage = err
                        )
                    } else latest
                }
            }
        )
    }

    private fun handleRetryLastMessage() {
        val currentState = _state.value
        if (currentState.isStreaming) return

        val activeRef = currentState.sessionRef ?: return
        val messages = currentState.messages

        val lastAssistantMsg = messages.lastOrNull { !it.isFromUser && it.isError } ?: return
        val lastUserMsg = messages.lastOrNull { it.isFromUser } ?: return
        val idempotencyKey = lastUserMsg.clientMessageId ?: return

        val retryRequest = RecommendationReq(
            sessionId = activeRef.remoteId,
            clientMessageId = idempotencyKey,
            query = lastUserMsg.text,
            latitude = currentState.userLatitude,
            longitude = currentState.userLongitude,
            radiusKm = 5.0,
            ignoreHealthProfile = false,
            provider = currentState.selectedProvider.name
        )

        _state.update { it.copy(errorMessage = null) }

        aiChatRepository.retryMessageInBackground(
            sessionRef = activeRef,
            assistantMessageId = lastAssistantMsg.id,
            request = retryRequest,
            onSessionPromoted = { pendingKey, newActiveRef ->
                _state.update { latest ->
                    if (latest.sessionRef?.cacheKey == pendingKey) {
                        latest.copy(sessionRef = newActiveRef)
                    } else latest
                }
            },
            onQuotaUpdated = { quotaEvent ->
                _state.update { it.copy(quotaState = it.quotaState.updateFromStream(quotaEvent.quota)) }
            },
            onErrorMessage = { failedSessionKey, err ->
                _state.update { latest ->
                    if (latest.sessionRef?.cacheKey == failedSessionKey) {
                        latest.copy(errorMessage = err)
                    } else latest
                }
            }
        )
    }

    private fun handleRetryMessages() {
        val activeSessionId = (_state.value.sessionRef as? AiSessionRef.Active)?.sessionId ?: return
        loadSessionMessages(activeSessionId, forceRefresh = true)
    }

    /**
     * Sol panel için geçmiş oturumları sayfalı olarak çeker.
     */
    private fun loadSessions(page: Int) {
        if (_state.value.isSessionsLoading) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSessionsLoading = true,
                    sessionsLoadError = null
                )
            }

            aiChatRepository.getUserSessions(page = page, size = 20)
                .onSuccess { slice ->
                    _state.update { current ->
                        current.copy(
                            isSessionsLoading = false,
                            currentSessionsPage = slice.number,
                            hasMoreSessions = !slice.last && !slice.empty,
                            sessionsLoadError = null
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isSessionsLoading = false,
                            sessionsLoadError = "Sohbet geçmişi yüklenemedi."
                        )
                    }
                }
        }
    }

    private fun loadOlderMessages() {
        val current = _state.value
        val sessionId = (current.sessionRef as? AiSessionRef.Active)?.sessionId ?: return
        if (current.isLoadingOlderMessages || !current.hasMoreOlderMessages || current.isMessagesLoading) return

        _state.update {
            it.copy(
                isLoadingOlderMessages = true,
                olderMessagesLoadError = null
            )
        }

        viewModelScope.launch {
            aiChatRepository.loadOlderMessages(sessionId)
                .onSuccess { hasMore ->
                    _state.update { latest ->
                        if (latest.sessionRef?.cacheKey == sessionId) {
                            latest.copy(
                                isLoadingOlderMessages = false,
                                hasMoreOlderMessages = hasMore
                            )
                        } else latest
                    }
                }
                .onFailure {
                    _state.update { latest ->
                        if (latest.sessionRef?.cacheKey == sessionId) {
                            latest.copy(
                                isLoadingOlderMessages = false,
                                olderMessagesLoadError = "Eski mesajlar yüklenemedi"
                            )
                        } else latest
                    }
                }
        }
    }

    private fun handleRetryLoadSessionAndQuota() {
        val currentState = _state.value
        if (currentState.isSessionsLoading) return

        val targetPage = if (currentState.sessions.isEmpty()) {
            loadQuotaStatus()
            0
        } else {
            currentState.currentSessionsPage + 1
        }

        loadSessions(targetPage)
    }

    /**
     * Sonsuz kaydırma ile bir sonraki oturum sayfasını yükler.
     */
    private fun handleLoadMoreSessions() {
        val currentState = _state.value
        if (!currentState.isSessionsLoading && currentState.hasMoreSessions) {
            loadSessions(page = currentState.currentSessionsPage + 1)
        }
    }

    /**
     * Tıklanan oturumun geçmiş mesajlarını ve mekan kartlarını getirir.
     */
    private fun loadSessionMessages(sessionId: String, forceRefresh: Boolean = false) {
        val targetRef = AiSessionRef.Active(sessionId)

        _state.update {
            it.copy(
                sessionRef = targetRef,
                isMessagesLoading = true,
                messagesLoadError = null,
                isLoadingOlderMessages = false,
                olderMessagesLoadError = null,
                hasMoreOlderMessages = false,
                previewPlace = null,
                isSidebarOpen = false
            )
        }

        viewModelScope.launch {
            aiChatRepository.loadSessionMessagesIfNeeded(sessionId, forceRefresh)
                .onSuccess { session ->
                    _state.update { latest ->
                        if (latest.sessionRef?.cacheKey == sessionId) {
                            latest.copy(
                                isMessagesLoading = false,
                                messagesLoadError = null,
                                hasMoreOlderMessages = session.hasMoreOlderMessages
                            )
                        } else latest
                    }
                }
                .onFailure { error ->
                    _state.update { latest ->
                        if (latest.sessionRef?.cacheKey == sessionId) {
                            latest.copy(
                                isMessagesLoading = false,
                                messagesLoadError = "Mesajlar yüklenemedi. İnternet bağlantını kontrol edip tekrar dene."
                            )
                        } else latest
                    }
                }
        }
    }

    /**
     * Yeni sohbet başlatır ve ekranı sıfırlar.
     */
    private fun handleNewChat() {
        _state.update {
            it.copy(
                sessionRef = null,
                messages = emptyList(),
                isStreaming = false,
                inputText = "",
                previewPlace = null,
                isSidebarOpen = false,
                isMessagesLoading = false,
                messagesLoadError = null,
                isLoadingOlderMessages = false,
                olderMessagesLoadError = null,
                hasMoreOlderMessages = false
            )
        }
    }

    /**
     * Oturumu siler; aktif oturum silindiyse sohbet ekranını sıfırlar.
     */
    private fun handleDeleteSession(sessionId: String) {
        viewModelScope.launch {
            aiChatRepository.deleteSession(sessionId)
                .onSuccess {
                    if (_state.value.activeRemoteSessionId == sessionId) {
                        handleNewChat()
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message ?: "Oturum silinemedi") }
                }
        }
    }

    private fun loadQuotaStatus() {
        viewModelScope.launch {
            aiChatRepository.getQuotaStatus()
                .onSuccess { quota ->
                    _state.update { it.copy(quotaState = quota.toUiState()) }
                }
        }
    }

}