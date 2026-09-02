package org.beem.tastymap.ui.map


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.beem.tastymap.map.TastyMapComponent
import org.beem.tastymap.permission.LocationPermissionWrapper
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.MapEvent
import org.beem.tastymap.map.MapScreenModel
import org.beem.tastymap.map.rememberTastyMapState
import org.beem.tastymap.search.state.SearchEvent
import org.beem.tastymap.search.SearchScreenModel
import org.beem.tastymap.search.model.toRestaurant
import org.beem.tastymap.ui.detailsheet.TastyDetailSheet
import org.beem.tastymap.ui.map.components.ActiveRouteBottomCard
import org.beem.tastymap.ui.map.components.SearchThisAreaChip
import org.beem.tastymap.ui.search.TastySearchOverlay


class TastyMapScreen : Screen {
    @Composable
    override fun Content() {
        val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

        val mapScreenModel: MapScreenModel = koinScreenModel()
        val searchScreenModel: SearchScreenModel = koinScreenModel()

        val mapState = rememberTastyMapState()

        val userLocation by mapScreenModel.userLocation.collectAsState()

        val selectedRestaurant = mapState.selectedRestaurant

        val activeRouteState by mapScreenModel.activeRoute.collectAsState()

        val searchUiState by searchScreenModel.uiState.collectAsState()

        var activeRestaurant by remember { mutableStateOf<Restaurant?>(null) }

        var showSearchThisArea by remember { mutableStateOf(false) }

        val isSearchingActive = searchUiState.query.isNotEmpty() || searchUiState.isDropdownVisible

        val localFocusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            searchScreenModel.event.collect { event ->
                when (event) {
                    is SearchEvent.HideKeyboard -> {
                        localFocusManager.clearFocus()
                    }
                    is SearchEvent.VenueSelected -> {
                        val venue = event.venue

                        if (venue.lat != null && venue.lng != null) {
                            mapState.centerOn(venue.lat, venue.lng, zoom = 16f)
                            mapState.showSearchPin(venue.lat, venue.lng)
                        }

                        activeRestaurant = venue.toRestaurant()
                    }
                }
            }
        }


        DisposableEffect(mapState) {
            mapState.onCameraIdleCallback = { lat, lng, zoom ->
                mapScreenModel.onCameraIdle(lat, lng, zoom)
            }
            onDispose {
                mapState.onCameraIdleCallback = null
            }
        }

        LaunchedEffect(Unit){
            mapScreenModel.event.collect{ event ->
                when(event){
                    is MapEvent.CenterOn -> {
                        mapState.centerOn(event.lat, event.lng, event.zoom)
                    }
                    is MapEvent.UserMarker -> {
                        mapState.userMarker(event.lat, event.lng, event.title, event.bearing)
                    }

                    is MapEvent.UpdateMapGeoSource -> {
                        mapState.updateMapData(event.source)
                    }

                    is MapEvent.OpenRestaurantDetails -> {
                        println("TastyMap UI -> OpenRestaurantDetails Eventi Geldi! Restoran: ${event.restaurant.name}")
                        activeRestaurant = event.restaurant
                    }
                    is MapEvent.DrawRoute -> {
                        mapState.drawRoute(
                            mainRoute = event.mainRouteCoordinates,
                            startConnector = event.startConnector,
                            endConnector = event.endConnector,
                            targetPlaceId = event.targetPlaceId
                        )
                    }
                    is MapEvent.ClearRoute -> {
                        mapState.clearRoute()
                    }
                    is MapEvent.ToggleSearchThisAreaButton -> {
                        showSearchThisArea = event.visible
                    }
                }
            }
            mapScreenModel.startObservingLocation()
        }

        LaunchedEffect(selectedRestaurant) {
            selectedRestaurant?.let {
                mapScreenModel.onMarkerClicked(it)
                mapState.clearSelectedRestaurant()
            }
        }

        LocationPermissionWrapper(
            onPermissionGranted = {
                mapScreenModel.startObservingLocation()
            }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
            ) {
                TastyMapComponent(
                    modifier = Modifier.fillMaxSize(),
                    mapUrl = myMaps,
                    state = mapState,
                    userLocation = userLocation,
                    onFabClicked = {
                        mapScreenModel.onCenterMapClicked()

                        mapScreenModel.fetchNearbyRestaurants(
                            userLocation.latitude,
                            userLocation.longitude
                        )
                    }
                )
                TastySearchOverlay(
                    onIntent = searchScreenModel::handleIntent,
                    uiState = searchUiState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )

                SearchThisAreaChip(
                    visible = showSearchThisArea && !isSearchingActive,
                    isLoading = false,
                    onClick = mapScreenModel::onSearchThisAreaClicked,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 74.dp)
                )

                ActiveRouteBottomCard(
                    routeState = activeRouteState,
                    onCloseRoute = {
                        mapScreenModel.clearRoute()
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                activeRestaurant?.let { restaurant ->
                    TastyDetailSheet(
                        restaurant = restaurant,
                        onDismiss = {
                            activeRestaurant = null
                            mapState.clearSelectedRestaurant()
                            mapState.clearSearchPin()
                        },
                        onDirectionsClick = {
                            val placeId = restaurant.id
                            val targetLat = restaurant.latitude
                            val targetLng = restaurant.longitude

                            activeRestaurant = null // Sheet'i kapat
                            mapState.clearSelectedRestaurant()

                            // Rotayı başlat
                            mapScreenModel.fetchDirections(
                                placeId = placeId,
                                targetLat = targetLat,
                                targetLng = targetLng
                            )
                        }
                    )
                }

            }
        }
    }

}