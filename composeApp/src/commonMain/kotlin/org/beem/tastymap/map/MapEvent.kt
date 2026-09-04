package org.beem.tastymap.map

import org.beem.tastymap.data.model.Restaurant

sealed class MapEvent{
    data class CenterOn(
        val lat: Double,
        val lng: Double,
        val zoom: Float? = null
    ) : MapEvent()
    data class UserMarker(
        val lat: Double,
        val lng: Double,
        val title: String,
        val bearing: Float,
    ) : MapEvent()
    data class UpdateMapGeoSource(
        val source: String
    ): MapEvent()
    data class OpenRestaurantDetails(val restaurant: Restaurant) : MapEvent()

    data class DrawRoute(
        val mainRouteCoordinates: List<List<Double>>,
        val startConnector: List<List<Double>> = emptyList(),
        val endConnector: List<List<Double>> = emptyList(),
        val formattedDistance: String,
        val formattedDuration: String,
        val targetLat: Double,
        val targetLng: Double,
        val targetPlaceId: String
    ) : MapEvent()

    data object ClearRoute : MapEvent()

    data class ToggleSearchThisAreaButton(val visible: Boolean) : MapEvent()

    data class ShowSelectedPin(val placeId: String, val lat: Double, val lng: Double) : MapEvent()
    data object ClearSelectedPin : MapEvent()
}
