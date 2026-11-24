package org.beem.tastymap.data.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.beem.tastymap.data.model.ai.AiSessionSummaryDto
import org.beem.tastymap.ui.ai.model.ChatMessage
import kotlin.collections.emptyMap

data class CachedAiSession(
    val messages: List<ChatMessage> = emptyList(),
    val isStreaming: Boolean = false,
    val hasError: Boolean = false,
    val hasMoreOlderMessages: Boolean = false,
    val nextBeforeId: Long? = null,
    val isInitialLoaded: Boolean = false
)

class AiChatMemoryCache {

    private val _sessionsMap = MutableStateFlow<Map<String, CachedAiSession>>(emptyMap())
    val sessionsMap: StateFlow<Map<String, CachedAiSession>> = _sessionsMap.asStateFlow()

    // Sidebar oturum listesi önbelleği
    private val _sessionSummaries = MutableStateFlow<List<AiSessionSummaryDto>>(emptyList())
    val sessionSummaries: StateFlow<List<AiSessionSummaryDto>> = _sessionSummaries.asStateFlow()

    fun getSession(sessionKey: String): CachedAiSession? {
        return _sessionsMap.value[sessionKey]
    }

    fun observeSession(sessionKey: String): Flow<CachedAiSession?> {
        return _sessionsMap
            .map { it[sessionKey] }
            .distinctUntilChanged()
    }

    fun putSession(sessionKey: String, session: CachedAiSession) {
        _sessionsMap.update { current ->
            current + (sessionKey to session)
        }
    }

    fun updateSession(sessionKey: String, transform: (CachedAiSession) -> CachedAiSession) {
        _sessionsMap.update { current ->
            val existing = current[sessionKey] ?: CachedAiSession()
            current + (sessionKey to transform(existing))
        }
    }

    /**
     * Backend'den gerçek sessionId geldiğinde geçici "pending_..." anahtarını
     * atomik olarak gerçek sessionId anahtarına taşır.
     */
    fun promotePendingSession(pendingKey: String, realSessionId: String) {
        if (pendingKey == realSessionId) return
        _sessionsMap.update { current ->
            val pendingData = current[pendingKey] ?: return@update current
            (current - pendingKey) + (realSessionId to pendingData)
        }
    }

    fun removeSession(sessionKey: String) {
        _sessionsMap.update { it - sessionKey }
        _sessionSummaries.update { list -> list.filterNot { it.sessionId == sessionKey } }
    }

    fun setSessionSummaries(summaries: List<AiSessionSummaryDto>, append: Boolean = false) {
        _sessionSummaries.update { current ->
            val combined = if (append) {
                val existingIds = current.map { it.sessionId }.toSet()
                current + summaries.filterNot { it.sessionId in existingIds }
            } else {
                summaries
            }
            combined.distinctBy { it.sessionId }
        }
    }

    fun prependOrUpdateSummary(summary: AiSessionSummaryDto) {
        _sessionSummaries.update { current ->
            listOf(summary) + current.filterNot { it.sessionId == summary.sessionId }
        }
    }

    fun moveSummaryToTop(sessionId: String, updatedAt: String) {
        _sessionSummaries.update { current ->
            val target = current.find { it.sessionId == sessionId } ?: return@update current
            listOf(target.copy(updatedAt = updatedAt)) + current.filterNot { it.sessionId == sessionId }
        }
    }

    fun clear() {
        _sessionsMap.value = emptyMap()
        _sessionSummaries.value = emptyList()
    }
}