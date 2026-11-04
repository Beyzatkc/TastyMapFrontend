package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiStreamPayloadDto(
    @SerialName("userId") val userId: Long? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("content") val content: String = "",
    @SerialName("hasMore") val hasMore: Boolean = false,
    @SerialName("remainingCount") val remainingCount: Int = 0
)