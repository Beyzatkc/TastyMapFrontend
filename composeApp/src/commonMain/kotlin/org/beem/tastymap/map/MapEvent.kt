package org.beem.tastymap.map

sealed class MapEvent{
    data class CenterOn(val lat: Double, val lng: Double) : MapEvent()
    data class UserMarker(
        val lat: Double,
        val lng: Double,
        val title: String,
        val bearing: Float,
    ) : MapEvent()
}
