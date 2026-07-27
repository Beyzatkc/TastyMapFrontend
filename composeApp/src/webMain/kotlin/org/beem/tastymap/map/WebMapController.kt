package org.beem.tastymap.map

import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.beem.tastymap.core.util.await
import org.beem.tastymap.data.model.Restaurant
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

class WebMapController : MapController {

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    suspend fun init(containerId: String, mapUrl: String): Result<Unit> {
        return runCatching {
            TastyMapBridge.loadMapLibreSdk().await()
            TastyMapBridge.initializeMap(containerId, mapUrl).await()
            Unit
        }
    }

    override fun animateTo(lat: Double, lng: Double, zoom: Float) {
        TastyMapBridge.flyTo(lat, lng, zoom.toDouble())
    }

    override fun addMarker(lat: Double, lng: Double, title: String) {
        // MapLibre yapımızda veriler GeoJSON kaynağı üzerinden basıldığı için
        // tekil marker ekleme işlemini doğrudan kaynak güncellemesiyle yönetiyoruz.
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


    @OptIn(ExperimentalWasmJsInterop::class)
    override fun setupRestaurantMarkerClickListener(
        onRestaurantSelected: (restaurant: Restaurant) -> Unit
    ) {
        val layerId = "restaurant-layer"

        addLayerHoverListener(layerId)

        addLayerClickListener(layerId) { jsonString ->
            println("Atlas: Wasm tarafında restoran tıklaması yakalandı -> $jsonString")

            val restaurant = parseAndSelectRestaurant(jsonString) ?: Restaurant(
                id = "", name = "", address = "", latitude = 0.0, longitude = 0.0,
                rating = 0.0, status = "", totalRatings = 0, types = emptyList(), category = ""
            )

            onRestaurantSelected(restaurant)
        }
    }


    private fun parseAndSelectRestaurant(jsonProperties: String): Restaurant? {
        return runCatching {
            val properties = jsonFormatter.decodeFromString<JsonObject>(jsonProperties)
            Restaurant(
                id = properties["id"]?.jsonPrimitive?.content.orEmpty(),
                name = properties["name"]?.jsonPrimitive?.content.orEmpty(),
                longitude = properties["longitude"]?.jsonPrimitive?.doubleOrNull
                    ?: properties["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull()
                    ?: 0.0,
                latitude = properties["latitude"]?.jsonPrimitive?.doubleOrNull
                    ?: properties["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull()
                    ?: 0.0,
                rating = properties["rating"]?.jsonPrimitive?.doubleOrNull
                    ?: properties["rating"]?.jsonPrimitive?.content?.toDoubleOrNull()
                    ?: 0.0,
                status = properties["status"]?.jsonPrimitive?.content.orEmpty(),
                category = properties["category"]?.jsonPrimitive?.content.orEmpty(),
                address = properties["address"]?.jsonPrimitive?.content.orEmpty()
            )
        }.getOrNull()
    }
}