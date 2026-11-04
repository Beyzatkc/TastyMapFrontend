package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiChatMessageResponse(
    @SerialName("id") val id: Long,
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("places") val places: List<HydratedPlaceCardDto> = emptyList()
)