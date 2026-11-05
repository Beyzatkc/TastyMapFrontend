package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.beem.tastymap.data.model.Restaurant

class TastyMapState {
    private val _controller = mutableStateOf<MapController?>(null)

    private var pendingJump: Triple<Double, Double, Float>? = null


    var controller: MapController?
        get() = _controller.value
        set(value) {
            _controller.value = value
            setupMarkerClickListener()
            setupCameraIdleListener()
            setupZoomListener()
            setupMapClickListener()
            startPendingJump()
        }

    var onCameraIdleCallback: ((lat: Double, lng: Double, zoom: Double) -> Unit)? = null

    var selectedRestaurant by mutableStateOf<Restaurant?>(null)
        private set

    var onZoomChangedCallback: (() -> Unit)? = null

    var onMapClickCallback: (() -> Unit)? = null


    fun centerOn(lat: Double, lng: Double, zoom: Float?){
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

    fun showSelectedPin(placeId: String, lat: Double, lng: Double) {
        controller?.showSelectedPin(placeId, lat, lng)
    }

    fun clearSelectedPin() {
        controller?.clearSelectedPin()
    }

    fun setupZoomListener() {
        controller?.setOnZoomChangedListener {
            onZoomChangedCallback?.invoke()
        }
    }

    fun setupMapClickListener() {
        controller?.setOnMapClickListener {
            onMapClickCallback?.invoke()
        }
    }

    fun jumpTo(lat: Double, lng: Double, zoom: Float) {
        val currentController = controller
        if (currentController != null) {
            currentController.jumpTo(lat, lng, zoom)
        } else {
            pendingJump = Triple(lat, lng, zoom)
        }
    }

    private fun startPendingJump() {
        pendingJump?.let { (lat, lng, zoom) ->
            jumpTo(lat, lng, zoom)
            pendingJump = null
        }
    }
}

@Composable
fun rememberTastyMapState(): TastyMapState{
    return remember { TastyMapState() }
}