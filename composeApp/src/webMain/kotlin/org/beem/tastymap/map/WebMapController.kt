package org.beem.tastymap.map

import org.beem.tastymap.data.model.Restaurant


class WebMapController: MapController {


    fun init(containerId: String) {
        initializeMapLibreJS(containerId)
    }

    override fun animateTo(lat: Double, lng: Double, zoom: Float) {
        flyToWeb(lat, lng, zoom)
    }


    override fun addMarker(lat: Double, lng: Double, title: String) {
    }

    override fun userMarker(
        lat: Double,
        lng: Double,
        title: String,
        bearing: Float
    ) {

    }

    override fun updateMapData(geoJson: String) {
        updateWebSource("restaurant-source", geoJson)
    }

    override fun onClickMarker(onMarkerClicked: (Restaurant) -> Unit) {
        listenMarkerClick("restaurant-layer") { id, name, lat, lng ->
            onMarkerClicked(Restaurant(
                id,
                name,
                longitude = lng,
                latitude = lat,
                cuisine = "",
                rating = 0.0
            ))
        }
    }
}