package org.beem.tastymap.ui.ai

import org.beem.tastymap.data.model.ai.AiProvider
import org.beem.tastymap.data.model.ai.AiSessionSummaryDto
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.ui.ai.model.AiQuotaState
import org.beem.tastymap.ui.ai.model.AiSessionRef
import org.beem.tastymap.ui.ai.model.ChatMessage

data class AiChatState(
    val sessionRef: AiSessionRef? = null,
    val inputText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isStreaming: Boolean = false,
    val currentStatusMessage: String? = null,
    val quotaState: AiQuotaState = AiQuotaState(),
    val isUpgradeSheetVisible: Boolean = false,

    val previewPlace: HydratedPlaceCardDto? = null,
    val selectedProvider: AiProvider = AiProvider.GROQ,

    val isMessagesLoading: Boolean = false,
    val messagesLoadError: String? = null,

    val isLoadingOlderMessages: Boolean = false,
    val hasMoreOlderMessages: Boolean = false,
    val olderMessagesLoadError: String? = null,

    val userLatitude: Double? = 37.9420,
    val userLongitude: Double? = 32.5150,

    val sessions: List<AiSessionSummaryDto> = emptyList(),
    val isSessionsLoading: Boolean = false,
    val sessionsLoadError: String? = null,
    val currentSessionsPage: Int = 0,
    val hasMoreSessions: Boolean = true,

    val isSidebarOpen: Boolean = false,

    val errorMessage: String? = null
) {
    val activeRemoteSessionId: String?
        get() = (sessionRef as? AiSessionRef.Active)?.sessionId

    val isNewChatSelected: Boolean
        get() = sessionRef !is AiSessionRef.Active
}