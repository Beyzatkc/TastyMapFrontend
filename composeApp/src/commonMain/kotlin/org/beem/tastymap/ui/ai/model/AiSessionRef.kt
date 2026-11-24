package org.beem.tastymap.ui.ai.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface AiSessionRef {
    val cacheKey: String
    val remoteId: String?

    @Immutable
    data class Pending(override val cacheKey: String) : AiSessionRef {
        override val remoteId: String? = null
    }

    @Immutable
    data class Active(val sessionId: String) : AiSessionRef {
        override val cacheKey: String = sessionId
        override val remoteId: String = sessionId
    }
}