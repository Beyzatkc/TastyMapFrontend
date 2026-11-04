package org.beem.tastymap.ui.ai

import org.beem.tastymap.data.model.ai.AiProvider
import org.beem.tastymap.data.model.ai.AiSessionSummaryDto
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto
import org.beem.tastymap.ui.ai.model.ChatMessage

data class AiChatState(

    val currentSessionId: String? = null,
    val inputText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isStreaming: Boolean = false,
    val isMessagesLoading: Boolean = false,
    val currentStatusMessage: String? = null,
    val remainingQuotaPercentage: Double = 100.0,

    val previewPlace: HydratedPlaceCardDto? = null,
    val selectedProvider: AiProvider = AiProvider.GROQ,

    val userLatitude: Double? = 37.9420,
    val userLongitude: Double? = 32.5150,

    val sessions: List<AiSessionSummaryDto> = emptyList(),
    val isSessionsLoading: Boolean = false,
    val currentSessionsPage: Int = 0,
    val hasMoreSessions: Boolean = true,
    val isSidebarOpen: Boolean = false,

    val errorMessage: String? = null
)