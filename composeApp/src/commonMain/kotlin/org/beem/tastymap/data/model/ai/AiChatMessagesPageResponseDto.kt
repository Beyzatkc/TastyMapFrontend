package org.beem.tastymap.data.model.ai

import kotlinx.serialization.Serializable


@Serializable
data class AiChatMessagesPageResponseDto(
    val messages: List<AiChatMessageResponse> = emptyList(),
    val hasMore: Boolean = false,
    val nextBeforeId: Long? = null
)