package org.beem.tastymap.ui.map.states

import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.model.ActiveRouteState

data class MapUiState(
    val userLocation: LocationData = LocationData(0.0, 0.0),
    val userBearing: Float = 0f,
    val cameraPosition: CameraPositionState = CameraPositionState(),
    val restaurantsGeoJson: String? = null,
    val routeRenderData: RouteRenderData? = null,
    val selectedPin: SelectedPinData? = null,
    val activeRestaurant: Restaurant? = null,
    val activeRoute: ActiveRouteState = ActiveRouteState.Idle,
    val searchChipState: SearchChipState = SearchChipState.HIDDEN
)