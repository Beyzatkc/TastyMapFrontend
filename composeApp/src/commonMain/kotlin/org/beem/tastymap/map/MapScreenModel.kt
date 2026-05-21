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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.model.GeoJson
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.repository.MapRepository
import org.beem.tastymap.ui.components.TastyMapIcon
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapScreenModel(
    private val locationTracker: LocationTracker,
    private val repository: MapRepository
): ScreenModel {
    val userLocation = locationTracker.locationState
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), LocationData(0.0, 0.0))

    private val _event = MutableSharedFlow<MapEvent>()

    private val jsonConfig = Json {
        encodeDefaults = true
        prettyPrint = false
    }

    private val ALPHA = 0.2f
    private var lastSmoothedLocation: LocationData? = null
    private var lastEmittedLocation: LocationData? = null
    private var lastValidBearing: Float = 0f
    private var lastEmittedBearing = 0f


    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant = _selectedRestaurant.asStateFlow()

    private val _showDetails = MutableStateFlow(false)
    val showDetails = _showDetails.asStateFlow()


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

    fun fetchNearbyRestaurants(lat: Double, lng: Double) {
        screenModelScope.launch {
            val request = MapRequest(
                lat = lat,
                lng = lng,
                radius = 500,
                keywords = listOf("restaurant", "meal_takeaway", "cafe")
            )
            val response = repository.searchMap(request)
            when(response){
                is ResultWrapper.Success -> {
                    _event.emit(MapEvent.UpdateMapGeoSource(
                        source = rebuildGeoJson(response.data.geoJson)
                    ))
                }
                is ResultWrapper.Error -> {
                    println("Hata Oluştu: ${response.message}")
                }
            }
        }
    }

    fun onMarkerClicked(restaurant: Restaurant) {
        repository.getPlaceById(restaurant.id)?.let { place ->
            restaurant.rating = place.rating
            restaurant.address = place.vicinity ?: ""
            restaurant.types = place.types
            restaurant.status = place.business_status ?: ""
            restaurant.latitude = place.geometry?.location?.lat ?: 0.0
            restaurant.longitude = place.geometry?.location?.lng ?: 0.0
            restaurant.totalRatings = place.user_ratings_total
        }
        _selectedRestaurant.value = restaurant
        println(_selectedRestaurant.value)
        _showDetails.value = true

        screenModelScope.launch{
            _event.emit(MapEvent.CenterOn(
                lat = restaurant.latitude,
                lng = restaurant.longitude,
                zoom = 17f
            ))
        }
    }

    fun closeDetails() {
        _showDetails.value = false
        _selectedRestaurant.value = null
    }

    private fun rebuildGeoJson(geoJson: GeoJson): String {
        val updatedFeatures = geoJson.features.map { feature ->
            val category = feature.properties.category

            val tastyMapIcon = TastyMapIcon.entries.find {
                it.name.lowercase() == category
            } ?: TastyMapIcon.DEFAULT

            val updatedProperties = feature.properties.copy(
                iconToUse = tastyMapIcon.iconName,
                iconScale = tastyMapIcon.iconScale
            )

            feature.copy(properties = updatedProperties)
        }

        val updatedGeoJson = geoJson.copy(features = updatedFeatures)

        return jsonConfig.encodeToString(updatedGeoJson)
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