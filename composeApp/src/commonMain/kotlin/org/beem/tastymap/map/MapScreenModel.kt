package org.beem.tastymap.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.util.GeoUtils
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.model.ActiveRouteState
import org.beem.tastymap.map.model.GeoJson
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.repository.MapRepository
import org.beem.tastymap.route.repository.RouteRepository
import org.beem.tastymap.ui.components.TastyMapIcon
import org.beem.tastymap.ui.map.MapIntent
import org.beem.tastymap.ui.map.states.CameraPositionState
import org.beem.tastymap.ui.map.states.MapUiState
import org.beem.tastymap.ui.map.states.RouteRenderData
import org.beem.tastymap.ui.map.states.SearchChipState
import org.beem.tastymap.ui.map.states.SelectedPinData
import org.beem.tastymap.ui.map.MapEvent as MapEvent
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class MapScreenModel(
    private val locationTracker: LocationTracker,
    private val repository: MapRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Tek seferlik kamera animasyonları için Channel (SharedFlow'dan daha güvenlidir, UI hazır olana kadar bekler)
    private val _event = Channel<MapEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

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

    private var activeRouteCoordinates: List<List<Double>> = emptyList()
    private var offRouteCount = 0
    private var isRerouting = false
    private val OFF_ROUTE_THRESHOLD_METERS = 35.0
    private val OFF_ROUTE_TRIGGER_COUNT = 2

    private var isFirstLocationFixed = false

    init {
        observeUserLocation()
    }

    // VIEW'DEN GELEN TÜM INTENT'LERİN TEK MERKEZİ
    fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.StartLocationTracking -> locationTracker.startTracking()
            is MapIntent.CenterOnUserClicked -> handleCenterOnUser()
            is MapIntent.SearchThisAreaClicked -> handleSearchThisArea()
            is MapIntent.CameraIdle -> handleCameraIdle(intent.lat, intent.lng, intent.zoom)
            is MapIntent.MarkerClicked -> handleMarkerClicked(intent.restaurant)
            is MapIntent.VenueSelectedFromSearch -> handleVenueSelectedFromSearch(intent.restaurant)
            is MapIntent.DismissRestaurantSheet -> handleDismissRestaurantSheet()
            is MapIntent.StartDirections -> fetchDirections(
                placeId = intent.placeId,
                targetLat = intent.targetLat,
                targetLng = intent.targetLng,
                forceRefresh = intent.forceRefresh
            )
            is MapIntent.ClearRoute -> handleClearRoute()
        }
    }

    private fun observeUserLocation() {
        viewModelScope.launch {
            locationTracker.locationState.collect { location ->
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

                    _uiState.update {
                        it.copy(
                            cameraPosition = CameraPositionState(
                                lat = filteredLocation.latitude,
                                lng = filteredLocation.longitude,
                                zoom = 15.5f
                            )
                        )
                    }
                    _event.send(
                        MapEvent.JumpCamera(
                            lat = filteredLocation.latitude,
                            lng = filteredLocation.longitude,
                            zoom = 15.5f
                        )
                    )
                    fetchNearbyRestaurants(filteredLocation.latitude, filteredLocation.longitude)
                }

                val distance = lastEmittedLocation?.let {
                    GeoUtils.distanceInMeters(
                        filteredLocation.latitude, filteredLocation.longitude,
                        it.latitude, it.longitude
                    )
                } ?: Double.MAX_VALUE

                val bearingDelta = abs(currentBearing - lastEmittedBearing)

                if (distance > 2.0 || bearingDelta > 10f) {
                    lastEmittedLocation = filteredLocation
                    lastEmittedBearing = currentBearing

                    _uiState.update {
                        it.copy(
                            userLocation = filteredLocation,
                            userBearing = currentBearing
                        )
                    }

                    checkOffRoute(filteredLocation)
                }
            }
        }
    }

    private fun checkOffRoute(userLoc: LocationData) {
        val currentRoute = _uiState.value.activeRoute
        if (currentRoute is ActiveRouteState.Active && activeRouteCoordinates.isNotEmpty() && !isRerouting) {
            val distanceToRoute = GeoUtils.minDistanceToRoute(
                userLat = userLoc.latitude,
                userLng = userLoc.longitude,
                routeCoordinates = activeRouteCoordinates
            )

            if (distanceToRoute > OFF_ROUTE_THRESHOLD_METERS) {
                offRouteCount++
                if (offRouteCount >= OFF_ROUTE_TRIGGER_COUNT) {
                    isRerouting = true
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

    private fun handleCenterOnUser() {
        viewModelScope.launch {
            val targetLoc = lastEmittedLocation ?: _uiState.value.userLocation
            if (targetLoc.latitude == 0.0 && targetLoc.longitude == 0.0) return@launch

            val currentZoom = _uiState.value.cameraPosition.zoom
            val targetZoom = if (currentZoom >= 14.0f) null else 15f

            _event.send(
                MapEvent.AnimateCamera(
                    lat = targetLoc.latitude,
                    lng = targetLoc.longitude,
                    zoom = targetZoom
                )
            )
        }
    }

    private fun handleCameraIdle(lat: Double, lng: Double, zoom: Double) {
        _uiState.update {
            it.copy(cameraPosition = CameraPositionState(lat, lng, zoom.toFloat()))
        }

        if (_uiState.value.activeRoute is ActiveRouteState.Active || searchJob?.isActive == true) return

        if (lastSearchedLat != 0.0 && lastSearchedLng != 0.0) {
            val distance = GeoUtils.distanceInMeters(lastSearchedLat, lastSearchedLng, lat, lng)
            if (distance > 400.0) {
                _uiState.update { it.copy(searchChipState = SearchChipState.ACTIONABLE) }
            }
        }
    }

    private fun handleSearchThisArea() {
        val cam = _uiState.value.cameraPosition
        if (cam.lat == 0.0 && cam.lng == 0.0) return
        fetchNearbyRestaurants(cam.lat, cam.lng)
    }

    private fun fetchNearbyRestaurants(lat: Double, lng: Double) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(searchChipState = SearchChipState.LOADING) }

            lastSearchedLat = lat
            lastSearchedLng = lng

            val request = MapRequest(
                lat = lat,
                lng = lng,
                radius = 500,
                keywords = listOf("restaurant", "meal_takeaway", "cafe")
            )

            when (val response = repository.searchMap(request)) {
                is ResultWrapper.Success -> {
                    val rebuiltJson = rebuildGeoJson(response.data.geoJson)
                    _uiState.update {
                        it.copy(
                            restaurantsGeoJson = rebuiltJson,
                            searchChipState = SearchChipState.SUCCESS
                        )
                    }
                    delay(1000.milliseconds)
                    _uiState.update { it.copy(searchChipState = SearchChipState.HIDDEN) }
                }
                is ResultWrapper.Error -> {
                    _uiState.update { it.copy(searchChipState = SearchChipState.ERROR) }
                    delay(1500.milliseconds)
                    _uiState.update { it.copy(searchChipState = SearchChipState.HIDDEN) }
                }
            }
        }
    }

    private fun handleMarkerClicked(restaurant: Restaurant) {
        viewModelScope.launch {
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

            _uiState.update {
                it.copy(
                    selectedPin = SelectedPinData(
                        placeId = updatedRestaurant.id,
                        lat = updatedRestaurant.latitude,
                        lng = updatedRestaurant.longitude
                    ),
                    activeRestaurant = updatedRestaurant
                )
            }

            _event.send(
                MapEvent.AnimateCamera(
                    lat = updatedRestaurant.latitude,
                    lng = updatedRestaurant.longitude
                )
            )
        }
    }

    private fun handleVenueSelectedFromSearch(restaurant: Restaurant) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedPin = SelectedPinData(
                        placeId = restaurant.id,
                        lat = restaurant.latitude,
                        lng = restaurant.longitude
                    ),
                    activeRestaurant = restaurant
                )
            }

            _event.send(
                MapEvent.AnimateCamera(
                    lat = restaurant.latitude,
                    lng = restaurant.longitude,
                    zoom = 16f
                )
            )
        }
    }

    private fun handleDismissRestaurantSheet() {
        _uiState.update {
            it.copy(
                activeRestaurant = null,
                selectedPin = null
            )
        }
    }

    private fun fetchDirections(
        placeId: String,
        targetLat: Double,
        targetLng: Double,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            val currentUserLoc = lastEmittedLocation ?: _uiState.value.userLocation
            if (currentUserLoc.latitude == 0.0 && currentUserLoc.longitude == 0.0) {
                _uiState.update {
                    it.copy(activeRoute = ActiveRouteState.Error("Konumunuz henüz alınamadı."))
                }
                return@launch
            }

            // Sheet'i ve seçili pini temizle, loading başlat
            _uiState.update { state ->
                state.copy(
                    activeRestaurant = null,
                    selectedPin = null,
                    activeRoute = if (state.activeRoute !is ActiveRouteState.Active) {
                        ActiveRouteState.Loading
                    } else {
                        state.activeRoute
                    }
                )
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

                        _uiState.update {
                            it.copy(
                                activeRoute = ActiveRouteState.Active(
                                    placeId = placeId,
                                    distance = routeData.formattedDistance,
                                    duration = routeData.formattedDuration,
                                    targetLat = targetLat,
                                    targetLng = targetLng
                                ),
                                routeRenderData = RouteRenderData(
                                    mainRoute = mainCoordinates,
                                    startConnector = startConnector,
                                    endConnector = endConnector,
                                    targetPlaceId = placeId
                                )
                            )
                        }
                    } else {
                        isRerouting = false
                        _uiState.update {
                            it.copy(activeRoute = ActiveRouteState.Error("Uygun bir rota bulunamadı."))
                        }
                    }
                }
                is ResultWrapper.Error -> {
                    isRerouting = false
                    _uiState.update {
                        it.copy(activeRoute = ActiveRouteState.Error(result.message ?: "Rota hesaplanamadı."))
                    }
                }
            }
        }
    }

    private fun handleClearRoute() {
        activeRouteCoordinates = emptyList()
        offRouteCount = 0
        isRerouting = false
        _uiState.update {
            it.copy(
                activeRoute = ActiveRouteState.Idle,
                routeRenderData = null
            )
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
        return jsonConfig.encodeToString(geoJson.copy(features = updatedFeatures))
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
}