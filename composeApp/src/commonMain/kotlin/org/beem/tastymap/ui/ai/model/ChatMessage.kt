package org.beem.tastymap.ui.ai.model

import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto

data class ChatMessage(
    val id: String = "",
    val text: String,
    val isFromUser: Boolean,
    val places: List<HydratedPlaceCardDto> = emptyList(),
    val statusMessage: String? = null,
    val isLoading: Boolean = false
)