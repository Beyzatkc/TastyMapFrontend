package org.beem.tastymap.ui.ai

import org.beem.tastymap.data.model.ai.AiProvider
import org.beem.tastymap.data.model.ai.HydratedPlaceCardDto

sealed interface AiChatIntent {
    data class InputTextChanged(val text: String) : AiChatIntent
    data object SendMessage : AiChatIntent
    data object NewChat : AiChatIntent
    data class SelectSession(val sessionId: String) : AiChatIntent
    data class DeleteSession(val sessionId: String) : AiChatIntent
    data object LoadMoreSessions : AiChatIntent
    data object ToggleSidebar : AiChatIntent
    data object NavigateBack : AiChatIntent
    data object DismissError : AiChatIntent

    data class ShowPlacePreview(val place: HydratedPlaceCardDto) : AiChatIntent
    data object DismissPlacePreview : AiChatIntent
    data class PlaceClicked(val placeDbId: Long) : AiChatIntent

    data class ShowOnMapClicked(val place: HydratedPlaceCardDto) : AiChatIntent
    data class DirectionsClicked(val place: HydratedPlaceCardDto) : AiChatIntent

    data class SelectProvider(val provider: AiProvider) : AiChatIntent
}