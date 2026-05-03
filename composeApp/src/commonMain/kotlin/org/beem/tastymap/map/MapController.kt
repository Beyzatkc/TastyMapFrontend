package org.beem.tastymap.map

import org.beem.tastymap.data.model.Restaurant

interface MapController {
    fun animateTo(lat: Double, lng: Double, zoom: Float = 15f)
    fun addMarker(lat: Double, lng: Double, title: String)
    fun userMarker(lat: Double, lng: Double, title: String, bearing: Float)
    fun updateMapData(geoJson: String)
    fun onClickMarker(onMarkerClicked: (Restaurant) -> Unit)

}