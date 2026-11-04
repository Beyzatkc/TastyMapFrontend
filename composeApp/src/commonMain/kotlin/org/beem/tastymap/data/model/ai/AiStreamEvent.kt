package org.beem.tastymap.data.model.ai

sealed interface AiStreamEvent {
    data class Status(val message: String) : AiStreamEvent
    data class Token(val chunk: String) : AiStreamEvent
    data class Places(val places: List<HydratedPlaceCardDto>) : AiStreamEvent
    data class Quota(val remainingPercentage: Double) : AiStreamEvent
    data object Completed : AiStreamEvent
    data class Error(val message: String) : AiStreamEvent
}