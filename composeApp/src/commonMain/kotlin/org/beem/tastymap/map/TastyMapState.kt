package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.beem.tastymap.data.model.Restaurant

class TastyMapState {
    private val _controller = mutableStateOf<MapController?>(null)

    var controller: MapController?
        get() = _controller.value
        set(value) {
            _controller.value = value
            setupMarkerClickListener()
            setupCameraIdleListener()
        }

    var onCameraIdleCallback: ((lat: Double, lng: Double, zoom: Double) -> Unit)? = null

    var selectedRestaurant by mutableStateOf<Restaurant?>(null)
        private set


    fun centerOn(lat: Double, lng: Double, zoom: Float = 15f){
        controller?.animateTo(lat, lng, zoom)
    }
    fun userMarker(lat: Double, lng: Double, title: String, bearing: Float){
        controller?.userMarker(lat, lng, title, bearing)
    }
    fun updateMapData(geoJson: String){
        controller?.updateMapData(geoJson)
    }

    fun setupMarkerClickListener() {
        controller?.setupRestaurantMarkerClickListener { restaurant ->
            selectedRestaurant = restaurant
        }
    }

    fun clearSelectedRestaurant() {
        selectedRestaurant = null
    }

    fun selectRestaurant(restaurant: Restaurant?) {
        selectedRestaurant = restaurant
    }

    private fun setupCameraIdleListener() {
        controller?.setOnCameraIdleListener { lat, lng, zoom ->
            onCameraIdleCallback?.invoke(lat, lng, zoom)
        }
    }

    fun drawRoute(
        mainRoute: List<List<Double>>,
        startConnector: List<List<Double>>,
        endConnector: List<List<Double>>,
        targetPlaceId: String?
    ) {
        controller?.drawRoute(
            mainRoute,
            startConnector,
            endConnector,
            targetPlaceId
        )
    }

    fun clearRoute() {
        controller?.clearRoute()
    }
}

@Composable
fun rememberTastyMapState(): TastyMapState{
    return remember { TastyMapState() }
}