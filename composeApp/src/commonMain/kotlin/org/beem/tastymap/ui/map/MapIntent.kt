package org.beem.tastymap.ui.map

import org.beem.tastymap.data.model.Restaurant

sealed interface MapIntent {
    data object StartLocationTracking : MapIntent
    data object CenterOnUserClicked : MapIntent
    data object SearchThisAreaClicked : MapIntent
    data class CameraIdle(val lat: Double, val lng: Double, val zoom: Double) : MapIntent
    data class MarkerClicked(val restaurant: Restaurant) : MapIntent
    data class VenueSelectedFromSearch(val restaurant: Restaurant) : MapIntent
    data object DismissRestaurantSheet : MapIntent
    data class StartDirections(
        val placeId: String,
        val targetLat: Double,
        val targetLng: Double,
        val forceRefresh: Boolean = false
    ) : MapIntent
    data object ClearRoute : MapIntent
}