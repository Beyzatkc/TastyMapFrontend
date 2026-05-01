package org.beem.tastymap.map

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.data.model.Restaurant
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapScreenModel(
    private val locationTracker: LocationTracker
): ScreenModel {
    val userLocation = locationTracker.locationState
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), LocationData(0.0, 0.0))

    private val _event = MutableSharedFlow<MapEvent>()

    private val ALPHA = 0.2f
    private var lastSmoothedLocation: LocationData? = null
    private var lastEmittedLocation: LocationData? = null
    private var lastValidBearing: Float = 0f
    private var lastEmittedBearing = 0f


    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants = _restaurants.asStateFlow()


    val event = _event.asSharedFlow()

    fun startObservingLocation(){
        locationTracker.startTracking()
    }

    init {
        screenModelScope.launch {
            userLocation.collect { location ->

                if (location.latitude == 0.0 && location.longitude == 0.0) return@collect

                val currentBearing = if (location.bearing != 0f) {
                    lastValidBearing = location.bearing
                    location.bearing
                } else {
                    lastValidBearing
                }

                val filteredLocation = applyLowPassFilter(location)

                val distance = if (lastEmittedLocation == null) {
                    Double.MAX_VALUE
                } else {
                    calculateDistance(
                        filteredLocation.latitude, filteredLocation.longitude,
                        lastEmittedLocation!!.latitude, lastEmittedLocation!!.longitude
                    )
                }

                val bearingDelta = abs(currentBearing - lastEmittedBearing)

                if (distance > 2.0 || bearingDelta > 10f) {
                    lastEmittedLocation = filteredLocation
                    lastEmittedBearing = currentBearing
                    _event.emit(MapEvent.UserMarker(
                        lat = filteredLocation.latitude,
                        lng = filteredLocation.longitude,
                        title = "Buradasınız",
                        bearing = currentBearing
                    ))
                }
            }
        }

        _restaurants.value = listOf(
            Restaurant("1", "Tiritçi Mithat", "Tirit", 37.874, 32.493, 4.8),
            Restaurant("2", "Ferah Etli Ekmek", "Etli Ekmek", 37.871, 32.485, 4.5),
            Restaurant("3", "Kuzucu Ali", "Kebap", 37.880, 32.500, 4.9)
        )
    }

    fun onCenterMapClicked(){
        screenModelScope.launch {
            lastEmittedLocation?.let { safeLocation ->
                _event.emit(MapEvent.CenterOn(
                    lat = safeLocation.latitude,
                    lng = safeLocation.longitude
                ))
            } ?: run{
                _event.emit(MapEvent.CenterOn(
                    lat = userLocation.value.latitude,
                    lng = userLocation.value.longitude
                ))
            }
        }
    }

    fun onRestaurantClicked(restaurantId: String) {
        val restaurant = _restaurants.value.find { it.id == restaurantId }
        restaurant?.let {
            screenModelScope.launch {
                _event.emit(MapEvent.CenterOn(it.latitude, it.longitude))
            }
        }
    }


    private fun applyLowPassFilter(newLocation: LocationData): LocationData {
        if (lastSmoothedLocation == null) {
            lastSmoothedLocation = newLocation
            return newLocation
        }

        val smoothedLat = ALPHA * newLocation.latitude + (1 - ALPHA) * lastSmoothedLocation!!.latitude
        val smoothedLng = ALPHA * newLocation.longitude + (1 - ALPHA) * lastSmoothedLocation!!.longitude

        val smoothedLocation = LocationData(smoothedLat, smoothedLng)
        lastSmoothedLocation = smoothedLocation
        return smoothedLocation
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0

        val dLat = (lat2 - lat1) * (PI / 180.0)
        val dLon = (lon2 - lon1) * (PI / 180.0)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * (PI / 180.0)) * cos(lat2 * (PI / 180.0)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

}