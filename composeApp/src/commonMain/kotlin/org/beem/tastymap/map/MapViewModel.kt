package org.beem.tastymap.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.beem.tastymap.data.model.LocationData
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel(
    private val locationTracker: LocationTracker
): ViewModel() {
    val userLocation = locationTracker.locationState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocationData(0.0, 0.0))

    private val _event = MutableSharedFlow<MapEvent>()

    private val ALPHA = 0.2f
    private var lastSmoothedLocation: LocationData? = null
    private var lastEmittedLocation: LocationData? = null
    private var lastValidBearing: Float = 0f
    private var lastEmittedBearing = 0f


    val event = _event.asSharedFlow()

    fun startObservingLocation(){
        locationTracker.startTracking()
    }

    init {
        viewModelScope.launch {
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
    }

    fun onCenterMapClicked(){
        viewModelScope.launch {
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