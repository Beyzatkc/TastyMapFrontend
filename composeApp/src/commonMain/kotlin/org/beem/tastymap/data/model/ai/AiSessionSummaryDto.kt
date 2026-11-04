package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiSessionSummaryDto(
    @SerialName("sessionId") val sessionId: String,
    @SerialName("title") val title: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)