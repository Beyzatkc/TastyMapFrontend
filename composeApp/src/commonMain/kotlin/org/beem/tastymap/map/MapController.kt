package org.beem.tastymap.map

import org.beem.tastymap.data.model.Restaurant

interface MapController {
    fun animateTo(lat: Double, lng: Double, zoom: Float?)
    fun addMarker(lat: Double, lng: Double, title: String)
    fun userMarker(lat: Double, lng: Double, title: String, bearing: Float)
    fun updateMapData(geoJson: String)
    fun setupRestaurantMarkerClickListener(onRestaurantSelected: (restaurant: Restaurant) -> Unit)

    fun drawRoute(
        mainRoute: List<List<Double>>,
        startConnector: List<List<Double>>,
        endConnector: List<List<Double>>,
        targetPlaceId: String?
    )
    fun clearRoute()

    fun setOnCameraIdleListener(onCameraIdle: (centerLat: Double, centerLng: Double, zoom: Double) -> Unit)

    fun showSelectedPin(placeId: String, lat: Double, lng: Double)
    fun clearSelectedPin()

    fun setOnZoomChangedListener(onZoomChanged: () -> Unit)

    fun setOnMapClickListener(onMapClick: () -> Unit)
}