package org.beem.tastymap.map

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.beem.tastymap.data.model.LocationData
import kotlin.js.JsAny

class WebLocationTracker: LocationTracker {

    private val _locationState = MutableStateFlow(LocationData(0.0, 0.0))

    override val locationState: StateFlow<LocationData> = _locationState.asStateFlow()

    private var watchId: Int? = null

    private val fakeLocationData = LocationData(
        latitude = 37.946978,
        longitude = 32.519940,
        accuracy = 0f,
        isAvailable = true
    )

    override fun startTracking() {
        _locationState.value = fakeLocationData

        val navigator = window.navigator as NavigatorWithGeolocation
        val geolocation = navigator.geolocation

        val success: (GeolocationPosition) -> Unit = { position ->
            val locationData = LocationData(
                latitude = position.coords.latitude,
                longitude = position.coords.longitude,
                accuracy = position.coords.accuracy.toFloat(),
                isAvailable = true
            )
            _locationState.value = fakeLocationData
            println("Konum bilgisi alındı: $locationData")
        }

        val error: (GeolocationPositionError) -> Unit = { err ->
            println("Konum hatası: ${err.message}")
        }

        watchId = geolocation.watchPosition(success, error)

    }

    override fun stopTracking() {
        TODO("Not yet implemented")
    }
}

external interface GeolocationPosition : JsAny {
    val coords: GeolocationCoordinates
}

external interface GeolocationCoordinates : JsAny {
    val latitude: Double
    val longitude: Double
    val accuracy: Double
}

external interface GeolocationPositionError : JsAny {
    val message: String
}

external interface NavigatorWithGeolocation : JsAny {
    val geolocation: GeolocationJs
}

external interface GeolocationJs : JsAny {
    fun getCurrentPosition(success: (GeolocationPosition) -> Unit, error: (GeolocationPositionError) -> Unit)
    fun watchPosition(success: (GeolocationPosition) -> Unit, error: (GeolocationPositionError) -> Unit): Int
    fun clearWatch(watchId: Int)
}