package org.beem.tastymap.map

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.beem.tastymap.data.model.Restaurant


class WebMapController: MapController {


    fun init(containerId: String, mapUrl: String) {
        TastyMapBridge.initializeMap(containerId, mapUrl)
    }

    override fun animateTo(lat: Double, lng: Double, zoom: Float) {
        TastyMapBridge.flyTo(lat, lng, zoom.toDouble())
    }


    override fun addMarker(lat: Double, lng: Double, title: String) {
    }

    override fun userMarker(
        lat: Double,
        lng: Double,
        title: String,
        bearing: Float
    ) {
        TastyMapBridge.updateUserMarker(lat, lng, bearing)
    }

    override fun updateMapData(geoJson: String) {
        TastyMapBridge.updateGeoJson("restaurant-source", geoJson)
    }

    override fun onClickMarker(onMarkerClicked: (Restaurant) -> Unit) {
        TastyMapBridge.onMarkerClick("restaurant-layer"){ jsonProperties->
            val json = Json { ignoreUnknownKeys = true }
            val properties = json.decodeFromString<JsonObject>(jsonProperties)

            val restaurant = Restaurant(
                id = properties["id"]?.jsonPrimitive?.content ?: "",
                name = properties["name"]?.jsonPrimitive?.content ?: "",
                longitude = properties["longitude"]?.jsonPrimitive?.content?.toDouble() ?: 0.0,
                latitude = properties["latitude"]?.jsonPrimitive?.content?.toDouble() ?: 0.0,
                rating = properties["rating"]?.jsonPrimitive?.content?.toDouble() ?: 0.0,
                status = properties["status"]?.jsonPrimitive?.content ?: "",
                category = properties["category"]?.jsonPrimitive?.content ?: "",
                address = properties["address"]?.jsonPrimitive?.content ?: ""
            )
            println(restaurant)
            onMarkerClicked(restaurant)
        }
    }
}