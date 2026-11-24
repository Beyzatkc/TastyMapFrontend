package org.beem.tastymap.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.beem.tastymap.core.provider.DispatcherProvider
import org.beem.tastymap.data.cache.AiChatMemoryCache
import org.beem.tastymap.data.cache.CachedAiSession
import org.beem.tastymap.data.model.ai.AiChatMessageResponse
import org.beem.tastymap.data.model.ai.AiSessionSummaryDto
import org.beem.tastymap.data.model.ai.AiStreamEvent
import org.beem.tastymap.data.model.ai.RecommendationReq
import org.beem.tastymap.data.model.ai.SliceResponse
import org.beem.tastymap.data.model.ai.UserQuotaStatusDto
import org.beem.tastymap.data.remote.AiChatDataSource
import org.beem.tastymap.ui.ai.model.AiSessionRef
import org.beem.tastymap.ui.ai.model.ChatMessage
import kotlin.time.Clock

class AiChatRepository(
    private val remoteDataSource: AiChatDataSource,
    private val memoryCache: AiChatMemoryCache,
    private val dispatchers: DispatcherProvider
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatchers.io)

    val sessionSummaries: StateFlow<List<AiSessionSummaryDto>> = memoryCache.sessionSummaries

    fun observeSession(sessionKey: String): Flow<CachedAiSession?> {
        return memoryCache.observeSession(sessionKey)
    }

    suspend fun getUserSessions(page: Int = 0, size: Int = 20): Result<SliceResponse<AiSessionSummaryDto>> {
        return withContext(dispatchers.io) {
            runCatching {
                remoteDataSource.getUserSessions(page, size)
            }.onSuccess { slice ->
                memoryCache.setSessionSummaries(slice.content, append = page > 0)
            }
        }
    }

    suspend fun loadSessionMessagesIfNeeded(
        sessionId: String,
        forceRefresh: Boolean = false,
        pageSize: Int = 20
    ): Result<CachedAiSession> {
        val cached = memoryCache.getSession(sessionId)

        if (cached?.isStreaming == true) {
            return Result.success(cached)
        }

        if (!forceRefresh && cached != null && cached.messages.isNotEmpty() && !cached.hasError) {
            return Result.success(cached)
        }

        return withContext(dispatchers.io) {
            runCatching {
                val page = remoteDataSource.getSessionMessages(
                    sessionId,
                    beforeId = null,
                    size = pageSize
                )
                val mapped = page.messages.map {
                    it.toDomain()
                }
                CachedAiSession(
                    messages = mapped,
                    isStreaming = false,
                    hasError = false,
                    hasMoreOlderMessages = page.hasMore,
                    nextBeforeId = page.nextBeforeId,
                    isInitialLoaded = true
                )
            }.onSuccess { session ->
                memoryCache.putSession(
                    sessionKey = sessionId,
                    session = session
                )
            }
        }
    }


    suspend fun loadOlderMessages(
        sessionId: String,
        pageSize: Int = 20
    ): Result<Boolean> {
        val empty = false
        val cached = memoryCache.getSession(sessionId)
            ?: return Result.success(empty)

        val cursorId = cached.nextBeforeId
        if (!cached.hasMoreOlderMessages || cursorId == null) {
            return Result.success(empty)
        }

        return withContext(dispatchers.io) {
            runCatching {
                val page = remoteDataSource.getSessionMessages(
                    sessionId = sessionId,
                    beforeId = cursorId,
                    size = pageSize
                )
                val olderMessages = page.messages.map { it.toDomain() }

                val latest = memoryCache.getSession(sessionId) ?: cached
                val merged = (olderMessages + latest.messages).distinctBy { it.id }

                memoryCache.putSession(
                    sessionKey = sessionId,
                    session = latest.copy(
                        messages = merged,
                        hasMoreOlderMessages = page.hasMore,
                        nextBeforeId = page.nextBeforeId
                    )
                )
                page.hasMore
            }
        }
    }

    fun sendMessageInBackground(
        initialRef: AiSessionRef,
        request: RecommendationReq,
        onSessionPromoted: (pendingKey: String, activeRef: AiSessionRef.Active) -> Unit,
        onQuotaUpdated: (AiStreamEvent.Quota) -> Unit,
        onErrorMessage: (sessionKey: String, errorMessage: String) -> Unit
    ) {
        var currentRef: AiSessionRef = initialRef
        val now = Clock.System.now().toEpochMilliseconds()

        val userMsgId = "user_$now"
        val assistantMsgId = "asst_${now + 1}"

        val userMessage = ChatMessage(
            id = userMsgId,
            clientMessageId = request.clientMessageId,
            text = request.query,
            isFromUser = true
        )

        val assistantMessage = ChatMessage(
            id = assistantMsgId,
            clientMessageId = request.clientMessageId,
            text = "",
            isFromUser = false,
            places = emptyList(),
            statusMessage = "İsteğiniz inceleniyor...",
            isLoading = true
        )

        memoryCache.updateSession(currentRef.cacheKey) { current ->
            current.copy(
                messages = current.messages + userMessage + assistantMessage,
                isStreaming = true
            )
        }

        collectStreamIntoCache(
            initialRef = initialRef,
            assistantMsgId = assistantMsgId,
            request = request,
            onSessionPromoted = onSessionPromoted,
            onQuotaUpdated = onQuotaUpdated,
            onErrorMessage = onErrorMessage
        )

    }

    fun retryMessageInBackground(
        sessionRef: AiSessionRef,
        assistantMessageId: String,
        request: RecommendationReq,
        onSessionPromoted: (pendingKey: String, activeRef: AiSessionRef.Active) -> Unit,
        onQuotaUpdated: (AiStreamEvent.Quota) -> Unit,
        onErrorMessage: (sessionKey: String, message: String) -> Unit
    ) {
        // Mevcut hatalı asistan balonunu sıfırla (yeni kullanıcı balonu ekleme!)
        memoryCache.updateSession(sessionRef.cacheKey) { current ->
            val resetMessages = current.messages.map { msg ->
                if (msg.id == assistantMessageId) {
                    msg.copy(
                        text = "",
                        places = emptyList(),
                        statusMessage = "Yeniden bağlanılıyor...",
                        isLoading = true,
                        isError = false
                    )
                } else msg
            }
            current.copy(
                messages = resetMessages,
                isStreaming = true,
                hasError = false
            )
        }

        collectStreamIntoCache(
            initialRef = sessionRef,
            assistantMsgId = assistantMessageId,
            request = request,
            onSessionPromoted = onSessionPromoted,
            onQuotaUpdated = onQuotaUpdated,
            onErrorMessage = onErrorMessage
        )
    }

    private fun collectStreamIntoCache(
        initialRef: AiSessionRef,
        assistantMsgId: String,
        request: RecommendationReq,
        onSessionPromoted: (pendingKey: String, activeRef: AiSessionRef.Active) -> Unit,
        onQuotaUpdated: (AiStreamEvent.Quota) -> Unit,
        onErrorMessage: (sessionKey: String, message: String) -> Unit
    ) {

        var currentRef: AiSessionRef = initialRef

        repositoryScope.launch {
            remoteDataSource.streamRecommendation(request)
                .catch { error ->
                    val errText = "Bağlantı kesildi. Yanıt tamamlanamadı."
                    memoryCache.updateSession(currentRef.cacheKey) { current ->
                        val updated = current.messages.map { msg ->
                            if (msg.id == assistantMsgId) {
                                val finalText = if (msg.text.isBlank()) errText else "${msg.text}\n\n $errText"
                                msg.copy(
                                    text = finalText,
                                    isLoading = false,
                                    statusMessage = null,
                                    isError = true
                                )
                            } else msg
                        }
                        current.copy(messages = updated, isStreaming = false, hasError = true)
                    }
                    onErrorMessage(currentRef.cacheKey, error.message ?: errText)
                }
                .collect { event ->
                    when (event) {
                        is AiStreamEvent.SessionCreated -> {
                            val realId = event.session.sessionId
                            val oldKey = currentRef.cacheKey
                            val activeRef = AiSessionRef.Active(realId)

                            memoryCache.promotePendingSession(oldKey, realId)
                            currentRef = activeRef
                            memoryCache.prependOrUpdateSummary(event.session)
                            onSessionPromoted(oldKey, activeRef)
                        }

                        is AiStreamEvent.Status -> {
                            memoryCache.updateSession(currentRef.cacheKey) { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(statusMessage = event.message)
                                    } else msg
                                }
                                current.copy(messages = updated)
                            }
                        }

                        is AiStreamEvent.Token -> {
                            memoryCache.updateSession(currentRef.cacheKey) { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(
                                            text = msg.text + event.chunk,
                                            isLoading = false,
                                            statusMessage = null,
                                            isError = false
                                        )
                                    } else msg
                                }
                                current.copy(messages = updated)
                            }
                        }

                        is AiStreamEvent.Places -> {
                            memoryCache.updateSession(currentRef.cacheKey) { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(places = event.places)
                                    } else msg
                                }
                                current.copy(messages = updated)
                            }
                        }

                        is AiStreamEvent.Quota -> {
                            onQuotaUpdated(event)
                        }

                        is AiStreamEvent.Completed -> {
                            memoryCache.updateSession(currentRef.cacheKey) { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        msg.copy(isLoading = false, statusMessage = null, isError = false)
                                    } else msg
                                }
                                current.copy(messages = updated, isStreaming = false, hasError = false)
                            }
                            val activeSession = currentRef as? AiSessionRef.Active
                            if (activeSession != null) {
                                memoryCache.moveSummaryToTop(activeSession.sessionId, todayIsoTimestamp())
                            }
                        }

                        is AiStreamEvent.Error -> {
                            val errText = event.message.ifBlank { "Bir sorun oluştu." }
                            memoryCache.updateSession(currentRef.cacheKey) { current ->
                                val updated = current.messages.map { msg ->
                                    if (msg.id == assistantMsgId) {
                                        val finalText = if (msg.text.isBlank()) errText else "${msg.text}\n\n⚠️ $errText"
                                        msg.copy(
                                            text = finalText,
                                            isLoading = false,
                                            statusMessage = null,
                                            isError = true
                                        )
                                    } else msg
                                }
                                current.copy(messages = updated, isStreaming = false, hasError = true)
                            }
                            onErrorMessage(currentRef.cacheKey, errText)
                        }
                    }
                }
        }

    }
    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return withContext(dispatchers.io) {
            runCatching {
                remoteDataSource.deleteSession(sessionId)
            }.onSuccess {
                memoryCache.removeSession(sessionId)
            }
        }
    }

    fun streamRecommendation(request: RecommendationReq): Flow<AiStreamEvent> {
        return remoteDataSource.streamRecommendation(request)
            .flowOn(dispatchers.io)
    }

    suspend fun getQuotaStatus(): Result<UserQuotaStatusDto> {
        return withContext(dispatchers.io) {
            runCatching {
                remoteDataSource.getQuotaStatus()
            }
        }
    }

    private fun todayIsoTimestamp(): String {
        val nowEpochSeconds = Clock.System.now().epochSeconds
        val todayEpochDays = ((nowEpochSeconds + 3 * 3600L) / 86_400L).toInt()
        val today = LocalDate.fromEpochDays(todayEpochDays)
        return "${today}T12:00:00"
    }

    private fun AiChatMessageResponse.toDomain() = ChatMessage(
        id = id.toString(),
        text = content,
        isFromUser = role == "USER",
        places = places
    )
}