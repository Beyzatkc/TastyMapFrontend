package org.beem.tastymap.ui.map


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.beem.tastymap.map.TastyMapComponent
import org.beem.tastymap.permission.LocationPermissionWrapper
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.beem.tastymap.map.MapEvent
import org.beem.tastymap.map.MapScreenModel
import org.beem.tastymap.map.rememberTastyMapState
import org.beem.tastymap.ui.components.TastyMapFab
import org.beem.tastymap.ui.tastyview.TastyBottomSheet
import org.beem.tastymap.ui.tastyview.TastyBottomSheetState
import org.beem.tastymap.ui.tastyview.buildRestaurantSheetUI
import org.beem.tastymap.ui.theme.TastyMapSheetPalette


class TastyMapScreen : Screen {
    @Composable
    override fun Content() {
        val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

        val mapScreenModel: MapScreenModel = koinScreenModel()
        val mapState = rememberTastyMapState()

        val userLocation by mapScreenModel.userLocation.collectAsState()

        val selectedRestaurant by mapScreenModel.selectedRestaurant.collectAsState()


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

                    is MapEvent.PlaceData -> {

                    }
                }
            }
            mapScreenModel.startObservingLocation()
        }

        mapState.onClickMarker { restaurant ->
            mapScreenModel.onMarkerClicked(restaurant)
        }

        LocationPermissionWrapper(
            onPermissionGranted = {
                mapScreenModel.startObservingLocation()
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()
            ) {
                TastyMapComponent(
                    modifier = Modifier.fillMaxSize(),
                    mapUrl = myMaps,
                    state = mapState,
                    userLocation = userLocation,
                )
                if (selectedRestaurant != null) {
                    val mySheetState = remember { TastyBottomSheetState() }
                    TastyBottomSheet(
                        widthPercentage = 90,
                        cornerRadius = 24,
                        backgroundColor = "#FFFFFF",
                        onDismiss = {
                            mapScreenModel.closeDetails()
                        },
                        sheetState = mySheetState,
                        content = {
                            buildRestaurantSheetUI(
                                restaurant = selectedRestaurant!!,
                                sheetState = mySheetState,
                                palette = TastyMapSheetPalette(),
                                onAction = { action ->
                                }
                            )
                        }
                    )
                    /*
                    RestaurantDetailSheet(
                        restaurant = selectedRestaurant!!,
                        onAction = { action ->
                            when (action) {
                                RestaurantAction.GetDirections ->{

                                }
                                RestaurantAction.Share ->{

                                }
                                RestaurantAction.ToggleFavorite ->{

                                }
                            }
                        }
                    ) {
                        mapScreenModel.closeDetails()
                    }

                     */
                }
                TastyMapFab(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = {
                        mapScreenModel.onCenterMapClicked()
                        mapScreenModel.fetchNearbyRestaurants(
                            userLocation.latitude,
                            userLocation.longitude
                        )
                    },
                    backgroundColor = "#00008B"
                )
            }
        }
    }

}