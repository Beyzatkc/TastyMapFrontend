package org.beem.tastymap.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.ai.AiStreamEvent
import org.beem.tastymap.data.model.ai.RecommendationReq
import org.beem.tastymap.data.repository.AiChatRepository
import org.beem.tastymap.ui.ai.model.ChatMessage
import kotlin.random.Random
import kotlin.time.Clock

class AiViewModel(
    private val aiChatRepository: AiChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state: StateFlow<AiChatState> = _state.asStateFlow()

    init {
        loadSessions(page = 0)
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
        }
    }


    private fun handleSendMessage() {
        val query = _state.value.inputText.trim()
        if (query.isBlank() || _state.value.isStreaming) return

        val timestamp = Random.nextLong(100000, 999999)
        val userMsgId = "user_$timestamp"
        val assistantMsgId = "asst_$timestamp"


        val userMessage = ChatMessage(
            id = userMsgId,
            text = query,
            isFromUser = true
        )

        val assistantMessage = ChatMessage(
            id = assistantMsgId,
            text = "",
            isFromUser = false,
            places = emptyList(),
            statusMessage = "İsteğiniz inceleniyor...",
            isLoading = true
        )

        val currentSessionId = _state.value.currentSessionId

        _state.update { current ->
            current.copy(
                inputText = "",
                isStreaming = true,
                previewPlace = null,
                messages = current.messages + userMessage + assistantMessage
            )
        }

        val request = RecommendationReq(
            sessionId = currentSessionId,
            query = query,
            latitude = _state.value.userLatitude,
            longitude = _state.value.userLongitude,
            radiusKm = 5.0,
            ignoreHealthProfile = false,
            provider = _state.value.selectedProvider.name
        )

        viewModelScope.launch {
            aiChatRepository.streamRecommendation(request)
                .catch { error ->
                    _state.update { current ->
                        val updated = current.messages.map { msg ->
                            if (msg.id == assistantMsgId) {
                                msg.copy(
                                    text = "Üzgünüm, bir bağlantı hatası oluştu.",
                                    isLoading = false,
                                    statusMessage = null
                                )
                            } else msg
                        }
                        current.copy(
                            isStreaming = false,
                            messages = updated,
                            errorMessage = error.message
                        )
                    }
                }
                .collect { event ->
                    when (event) {
                        is AiStreamEvent.Status -> {
                            _state.update { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(statusMessage = event.message)
                                    } else msg
                                }
                                current.copy(messages = updated)
                            }
                        }

                        is AiStreamEvent.Token -> {
                            _state.update { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(
                                            text = msg.text + event.chunk,
                                            isLoading = false,
                                            statusMessage = null
                                        )
                                    } else msg
                                }
                                current.copy(messages = updated)
                            }
                        }

                        is AiStreamEvent.Places -> {
                            _state.update { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(places = event.places)
                                    } else msg
                                }
                                current.copy(messages = updated)
                            }
                        }

                        is AiStreamEvent.Quota -> {
                            _state.update { it.copy(remainingQuotaPercentage = event.remainingPercentage) }
                        }

                        is AiStreamEvent.Completed -> {
                            _state.update { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(isLoading = false, statusMessage = null)
                                    } else msg
                                }
                                current.copy(
                                    isStreaming = false,
                                    messages = updated
                                )
                            }
                            if (currentSessionId == null) {
                                loadSessions(page = 0)
                            }
                        }

                        is AiStreamEvent.Error -> {
                            _state.update { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(
                                            text = event.message.ifBlank { "Bir sorun oluştu." },
                                            isLoading = false,
                                            statusMessage = null
                                        )
                                    } else msg
                                }
                                current.copy(
                                    isStreaming = false,
                                    messages = updated,
                                    errorMessage = event.message
                                )
                            }
                        }
                    }
                }
        }
    }

    /**
     * Sol panel için geçmiş oturumları sayfalı olarak çeker.
     */
    private fun loadSessions(page: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isSessionsLoading = true) }

            aiChatRepository.getUserSessions(page = page, size = 20)
                .onSuccess { slice ->
                    _state.update { current ->
                        val updatedList = if (page == 0) {
                            slice.content
                        } else {
                            current.sessions + slice.content
                        }

                        current.copy(
                            sessions = updatedList,
                            isSessionsLoading = false,
                            currentSessionsPage = slice.number,
                            hasMoreSessions = !slice.last && !slice.empty
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSessionsLoading = false, errorMessage = error.message) }
                }
        }
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
    private fun loadSessionMessages(sessionId: String) {
        if (_state.value.currentSessionId == sessionId) {
            _state.update { it.copy(isSidebarOpen = false) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    currentSessionId = sessionId,
                    isMessagesLoading = true,
                    isSidebarOpen = false
                )
            }

            aiChatRepository.getSessionMessages(sessionId)
                .onSuccess { dtoList ->
                    val uiMessages = dtoList.map { dto ->
                        ChatMessage(
                            id = dto.id.toString(),
                            text = dto.content,
                            isFromUser = dto.role == "USER",
                            places = dto.places
                        )
                    }
                    _state.update {
                        it.copy(
                            messages = uiMessages,
                            isMessagesLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isMessagesLoading = false,
                            errorMessage = error.message ?: "Mesajlar yüklenemedi"
                        )
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
                currentSessionId = null,
                messages = emptyList(),
                inputText = "",
                isSidebarOpen = false
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
                    _state.update { current ->
                        val filteredSessions = current.sessions.filterNot { it.sessionId == sessionId }
                        val resetChat = current.currentSessionId == sessionId

                        current.copy(
                            sessions = filteredSessions,
                            currentSessionId = if (resetChat) null else current.currentSessionId,
                            messages = if (resetChat) emptyList() else current.messages
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message ?: "Oturum silinemedi") }
                }
        }
    }
}