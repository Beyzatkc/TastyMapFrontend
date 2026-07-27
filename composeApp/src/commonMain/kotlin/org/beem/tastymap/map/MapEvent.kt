package org.beem.tastymap.map

import org.beem.tastymap.data.model.Restaurant

sealed class MapEvent{
    data class CenterOn(
        val lat: Double,
        val lng: Double,
        val zoom: Float = 15f
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
}
