package org.beem.tastymap.map

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import org.beem.tastymap.core.util.GeoUtils
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.model.ActiveRouteState
import org.beem.tastymap.map.model.GeoJson
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.repository.MapRepository
import org.beem.tastymap.route.repository.RouteRepository
import org.beem.tastymap.route.repository.RouteRepositoryImpl
import org.beem.tastymap.ui.components.TastyMapIcon
import org.beem.tastymap.ui.map.states.SearchChipState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

class MapScreenModel(
    private val locationTracker: LocationTracker,
    private val repository: MapRepository,
    private val routeRepository: RouteRepositoryImpl
): ScreenModel {
    val userLocation = locationTracker.locationState
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), LocationData(0.0, 0.0))

    private val _event = MutableSharedFlow<MapEvent>()

    private val jsonConfig = Json {
        encodeDefaults = true
        prettyPrint = false
    }

    private var searchJob: Job? = null

    private val ALPHA = 0.2f
    private var lastSmoothedLocation: LocationData? = null
    private var lastEmittedLocation: LocationData? = null
    private var lastValidBearing: Float = 0f
    private var lastEmittedBearing = 0f

    private var lastSearchedLat: Double = 0.0
    private var lastSearchedLng: Double = 0.0
    private var currentCameraLat: Double = 0.0
    private var currentCameraLng: Double = 0.0
    private var currentCameraZoom: Double = 0.0

    private var activeRouteCoordinates: List<List<Double>> = emptyList()
    private var offRouteCount = 0
    private var isRerouting = false
    private val OFF_ROUTE_THRESHOLD_METERS = 35.0
    private val OFF_ROUTE_TRIGGER_COUNT = 2


    private val _activeRoute = MutableStateFlow<ActiveRouteState>(ActiveRouteState.Idle)
    val activeRoute = _activeRoute.asStateFlow()

    private val _searchChipState = MutableStateFlow(SearchChipState.HIDDEN)
    val searchChipState = _searchChipState.asStateFlow()

    private var isFirstLocationFixed = false

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

                if (!isFirstLocationFixed) {
                    isFirstLocationFixed = true

                    _event.emit(
                        MapEvent.JumpTo(
                            lat = filteredLocation.latitude,
                            lng = filteredLocation.longitude,
                            zoom = 15.5f
                        )
                    )

                    fetchNearbyRestaurants(
                        lat = filteredLocation.latitude,
                        lng = filteredLocation.longitude
                    )
                }

                val distance = if (lastEmittedLocation == null) {
                    Double.MAX_VALUE
                } else {
                    GeoUtils.distanceInMeters(
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


                    val currentRoute = _activeRoute.value
                    if (currentRoute is ActiveRouteState.Active && activeRouteCoordinates.isNotEmpty() && !isRerouting) {
                        val distanceToRoute = GeoUtils.minDistanceToRoute(
                            userLat = filteredLocation.latitude,
                            userLng = filteredLocation.longitude,
                            routeCoordinates = activeRouteCoordinates
                        )

                        if (distanceToRoute > OFF_ROUTE_THRESHOLD_METERS) {
                            offRouteCount++
                            println("TastyMap Navigation -> Rotadan Sapıldı: $distanceToRoute m (Sayaç: $offRouteCount)")

                            if (offRouteCount >= OFF_ROUTE_TRIGGER_COUNT) {
                                isRerouting = true
                                println("TastyMap Navigation -> Yeni rota isteniyor...")
                                fetchDirections(
                                    placeId = currentRoute.placeId,
                                    targetLat = currentRoute.targetLat,
                                    targetLng = currentRoute.targetLng,
                                    forceRefresh = true
                                )
                            }
                        } else {
                            offRouteCount = 0
                        }
                    }
                }
            }
        }
    }

    fun onCenterMapClicked(){
        screenModelScope.launch {
            val targetLoc = lastEmittedLocation ?: userLocation.value
            val targetZoom = if (currentCameraZoom >= 14.0) null else 15f

            _event.emit(MapEvent.CenterOn(
                lat = targetLoc.latitude,
                lng = targetLoc.longitude,
                zoom = targetZoom
            ))
        }
    }

    fun fetchNearbyRestaurants(lat: Double, lng: Double) {
        searchJob?.cancel()
        searchJob = screenModelScope.launch {

            _searchChipState.value = SearchChipState.LOADING

            lastSearchedLat = lat
            lastSearchedLng = lng

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

                    _searchChipState.value = SearchChipState.SUCCESS

                    delay(1000.milliseconds)
                    _searchChipState.value = SearchChipState.HIDDEN
                }
                is ResultWrapper.Error -> {
                    println("Hata Oluştu: ${response.message}")

                    _searchChipState.value = SearchChipState.ERROR
                    delay(1500.milliseconds)
                    _searchChipState.value = SearchChipState.HIDDEN
                }
            }
        }
    }


    fun onCameraIdle(lat: Double, lng: Double, zoom: Double) {
        currentCameraLat = lat
        currentCameraLng = lng
        currentCameraZoom = zoom

        if (_activeRoute.value is ActiveRouteState.Active || searchJob?.isActive == true) return

        if (lastSearchedLat != 0.0 && lastSearchedLng != 0.0) {
            val distance = GeoUtils.distanceInMeters(lastSearchedLat, lastSearchedLng, lat, lng)
            if (distance > 400.0) {
                screenModelScope.launch {
                    _searchChipState.value = SearchChipState.ACTIONABLE
                }
            }
        }
    }

    fun onSearchThisAreaClicked() {
        if (currentCameraLat == 0.0 && currentCameraLng == 0.0) return
        fetchNearbyRestaurants(currentCameraLat, currentCameraLng)
    }

    fun onMarkerClicked(restaurant: Restaurant) {
        screenModelScope.launch{
            val updatedRestaurant = repository.getPlaceById(restaurant.id)?.let { place ->
                restaurant.copy(
                    rating = place.rating,
                    address = place.vicinity ?: "",
                    types = place.types,
                    status = place.business_status ?: "",
                    latitude = place.geometry?.location?.lat ?: 0.0,
                    longitude = place.geometry?.location?.lng ?: 0.0,
                    totalRatings = place.user_ratings_total
                )
            } ?: restaurant

            _event.emit(MapEvent.CenterOn(
                lat = updatedRestaurant.latitude,
                lng = updatedRestaurant.longitude
            ))

            _event.emit(MapEvent.ShowSelectedPin(
                placeId = updatedRestaurant.id,
                lat = updatedRestaurant.latitude,
                lng = updatedRestaurant.longitude
            ))

            _event.emit(MapEvent.OpenRestaurantDetails(updatedRestaurant))
        }
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

    fun fetchDirections(
        placeId: String,
        targetLat: Double,
        targetLng: Double,
        forceRefresh: Boolean = false
    ) {
        screenModelScope.launch {
            val currentUserLoc = lastEmittedLocation ?: userLocation.value
            if (currentUserLoc.latitude == 0.0 && currentUserLoc.longitude == 0.0) {
                _activeRoute.value = ActiveRouteState.Error("Konumunuz henüz alınamadı.")
                return@launch
            }

            // Yeniden hesaplanırken kullanıcı kartı yok olmasın
            if (_activeRoute.value !is ActiveRouteState.Active) {
                _activeRoute.value = ActiveRouteState.Loading
            }

            when (val result = routeRepository.fetchRoute(placeId, currentUserLoc.latitude, currentUserLoc.longitude, forceRefresh)) {
                is ResultWrapper.Success -> {
                    val routeData = result.data.data
                    if (routeData != null && routeData.coordinates.isNotEmpty()) {
                        val mainCoordinates = routeData.coordinates

                        activeRouteCoordinates = mainCoordinates
                        offRouteCount = 0
                        isRerouting = false

                        val firstPoint = mainCoordinates.first()
                        val userPoint = listOf(currentUserLoc.longitude, currentUserLoc.latitude)
                        val startConnector = listOf(userPoint, firstPoint)

                        val lastPoint = mainCoordinates.last()
                        val targetPoint = listOf(targetLng, targetLat)
                        val endConnector = listOf(lastPoint, targetPoint)

                        _activeRoute.value = ActiveRouteState.Active(
                            placeId = placeId,
                            distance = routeData.formattedDistance,
                            duration = routeData.formattedDuration,
                            targetLat = targetLat,
                            targetLng = targetLng
                        )

                        _event.emit(
                            MapEvent.DrawRoute(
                                mainRouteCoordinates = mainCoordinates,
                                startConnector = startConnector,
                                endConnector = endConnector,
                                formattedDistance = routeData.formattedDistance,
                                formattedDuration = routeData.formattedDuration,
                                targetLat = targetLat,
                                targetLng = targetLng,
                                targetPlaceId = placeId
                            )
                        )
                    } else {
                        isRerouting = false
                        _activeRoute.value = ActiveRouteState.Error("Uygun bir rota bulunamadı.")
                    }
                }
                is ResultWrapper.Error -> {
                    isRerouting = false
                    _activeRoute.value = ActiveRouteState.Error(result.message ?: "Rota hesaplanamadı.")
                }
            }
        }
    }

    fun clearRoute() {
        screenModelScope.launch {
            activeRouteCoordinates = emptyList()
            offRouteCount = 0
            isRerouting = false
            _activeRoute.value = ActiveRouteState.Idle
            _event.emit(MapEvent.ClearRoute)
        }
    }

}