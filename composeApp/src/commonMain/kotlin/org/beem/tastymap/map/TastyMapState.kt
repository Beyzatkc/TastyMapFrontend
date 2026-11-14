package org.beem.tastymap.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.map.states.MapUiState

class TastyMapState {
    private val _controller = mutableStateOf<MapController?>(null)
    private var lastSyncedState: MapUiState? = null
    private var pendingJump: Triple<Double, Double, Float>? = null


    var controller: MapController?
        get() = _controller.value
        set(value) {
            _controller.value = value
            if (value != null) {
                setupMarkerClickListener()
                setupCameraIdleListener()
                setupZoomListener()
                setupMapClickListener()

                val pending = pendingJump
                if (pending != null) {
                    value.jumpTo(pending.first, pending.second, pending.third)
                } else {
                    lastSyncedState?.cameraPosition?.let { cam ->
                        value.jumpTo(cam.lat, cam.lng, cam.zoom)
                    }
                }

                lastSyncedState?.let {
                    restoreFullState(value, it)
                }
            }

        }

    var onCameraIdleCallback: ((lat: Double, lng: Double, zoom: Double) -> Unit)? = null

    var selectedRestaurant by mutableStateOf<Restaurant?>(null)
        private set

    var onZoomChangedCallback: (() -> Unit)? = null

    var onMapClickCallback: (() -> Unit)? = null


    fun syncWith(newState: MapUiState) {
        val prevState = lastSyncedState
        lastSyncedState = newState

        val activeController = controller ?: return // Controller henüz hazır değilse state saklandı, bağlanınca basılacak!

        // 1. Restoran GeoJSON değiştiyse güncelle
        if (prevState?.restaurantsGeoJson != newState.restaurantsGeoJson && newState.restaurantsGeoJson != null) {
            activeController.updateMapData(newState.restaurantsGeoJson)
        }

        // 2. Aktif Rota değiştiyse çiz veya temizle
        if (prevState?.routeRenderData != newState.routeRenderData) {
            val route = newState.routeRenderData
            if (route != null) {
                activeController.drawRoute(
                    mainRoute = route.mainRoute,
                    startConnector = route.startConnector,
                    endConnector = route.endConnector,
                    targetPlaceId = route.targetPlaceId
                )
            } else {
                activeController.clearRoute()
            }
        }

        // 3. Seçili Pin değiştiyse göster veya temizle
        if (prevState?.selectedPin != newState.selectedPin) {
            val pin = newState.selectedPin
            if (pin != null) {
                activeController.showSelectedPin(pin.placeId, pin.lat, pin.lng)
            } else {
                activeController.clearSelectedPin()
            }
        }

        // 4. Kullanıcı konumu veya yönü değiştiyse marker'ı kaydır
        if (prevState?.userLocation != newState.userLocation || prevState?.userBearing != newState.userBearing) {
            if (newState.userLocation.latitude != 0.0 && newState.userLocation.longitude != 0.0) {
                activeController.userMarker(
                    lat = newState.userLocation.latitude,
                    lng = newState.userLocation.longitude,
                    title = "Buradasınız",
                    bearing = newState.userBearing
                )
            }
        }
    }


    private fun restoreFullState(activeController: MapController, state: MapUiState) {
        state.restaurantsGeoJson?.let {
            activeController.updateMapData(it)
        }

        state.routeRenderData?.let { route ->
            activeController.drawRoute(
                mainRoute = route.mainRoute,
                startConnector = route.startConnector,
                endConnector = route.endConnector,
                targetPlaceId = route.targetPlaceId
            )
        }

        state.selectedPin?.let { pin ->
            activeController.showSelectedPin(pin.placeId, pin.lat, pin.lng)
        }

        if (state.userLocation.latitude != 0.0 && state.userLocation.longitude != 0.0) {
            activeController.userMarker(
                lat = state.userLocation.latitude,
                lng = state.userLocation.longitude,
                title = "Buradasınız",
                bearing = state.userBearing
            )
        }
    }


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

@Composable
fun rememberTastyMapState(uiState: MapUiState): TastyMapState {
    val mapState = remember { TastyMapState() }
    SideEffect {
        mapState.syncWith(uiState)
    }
    return mapState
}