package org.beem.tastymap.map.model

sealed interface ActiveRouteState {
    data object Idle : ActiveRouteState
    data object Loading : ActiveRouteState
    data class Active(
        val placeId: String,
        val distance: String,
        val duration: String,
        val targetLat: Double,
        val targetLng: Double
    ) : ActiveRouteState
    data class Error(val message: String) : ActiveRouteState
}