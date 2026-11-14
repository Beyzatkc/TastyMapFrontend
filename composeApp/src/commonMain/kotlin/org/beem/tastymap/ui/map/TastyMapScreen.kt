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
import org.beem.tastymap.ui.map.states.SearchChipState
import org.beem.tastymap.ui.search.TastySearchOverlay
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun TastyMapScreen() {
    val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

    val mapScreenModel: MapScreenModel = koinViewModel<MapScreenModel>()
    val searchScreenModel: SearchScreenModel = koinViewModel<SearchScreenModel>()

    val uiState by mapScreenModel.uiState.collectAsState()
    val mapState = rememberTastyMapState(uiState = uiState)

    val selectedRestaurant = mapState.selectedRestaurant
    val searchUiState by searchScreenModel.uiState.collectAsState()
    val currentActiveRestaurant by rememberUpdatedState(uiState.activeRestaurant)
    var collapseSheetTrigger by remember { mutableStateOf(0) }
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
                        mapState.showSelectedPin(
                            placeId = venue.placeId,
                            lat = venue.lat,
                            lng = venue.lng
                        )
                        mapScreenModel.onIntent(MapIntent.VenueSelectedFromSearch(venue.toRestaurant()))
                    }
                }
            }
        }
    }


        DisposableEffect(mapState) {
            mapState.onZoomChangedCallback = {
                if (currentActiveRestaurant != null) {
                    collapseSheetTrigger++
                }
            }

            mapState.onMapClickCallback = {
                if (currentActiveRestaurant != null) {
                    collapseSheetTrigger++
                }
            }

            mapState.onCameraIdleCallback = { lat, lng, zoom ->
                mapScreenModel.onIntent(MapIntent.CameraIdle(lat, lng, zoom))
            }
            onDispose {
                mapState.onZoomChangedCallback = null
                mapState.onCameraIdleCallback = null
            }
        }

        LaunchedEffect(Unit){
            mapScreenModel.event.collect{ event ->
                when(event){
                    is MapEvent.AnimateCamera -> mapState.centerOn(event.lat, event.lng, event.zoom)
                    is MapEvent.JumpCamera -> mapState.jumpTo(event.lat, event.lng, event.zoom)
                }
            }
        }

        LaunchedEffect(selectedRestaurant) {
            selectedRestaurant?.let {
                mapScreenModel.onIntent(MapIntent.MarkerClicked(it))
                mapState.clearSelectedRestaurant()
            }
        }

        LocationPermissionWrapper(
            onPermissionGranted = {
                mapScreenModel.onIntent(MapIntent.StartLocationTracking)
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
                    userLocation = uiState.userLocation,
                    onFabClicked = {
                        mapScreenModel.onIntent(MapIntent.CenterOnUserClicked)
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
                    state = if (isSearchingActive) SearchChipState.HIDDEN else uiState.searchChipState,
                    onClick = { mapScreenModel.onIntent(MapIntent.SearchThisAreaClicked) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 74.dp)
                )

                ActiveRouteBottomCard(
                    routeState = uiState.activeRoute,
                    onCloseRoute = {
                        mapScreenModel.onIntent(MapIntent.ClearRoute)
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                uiState.activeRestaurant?.let { restaurant ->
                    TastyDetailSheet(
                        restaurant = restaurant,
                        onDismiss = {
                            mapScreenModel.onIntent(MapIntent.DismissRestaurantSheet)
                            mapState.clearSelectedRestaurant()
                            mapState.clearSelectedPin()
                        },
                        collapseToPeekTrigger = collapseSheetTrigger,
                        onDirectionsClick = {
                            mapState.clearSelectedRestaurant()
                            mapState.clearSelectedPin()
                            mapScreenModel.onIntent(
                                MapIntent.StartDirections(
                                    placeId = restaurant.id,
                                    targetLat = restaurant.latitude,
                                    targetLng = restaurant.longitude
                                )
                            )
                        }
                    )
                }

            }
        }
    }