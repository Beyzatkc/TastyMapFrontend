package org.beem.tastymap.ui.map


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.beem.tastymap.map.TastyMapComponent
import org.beem.tastymap.permission.LocationPermissionWrapper
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.MapEvent
import org.beem.tastymap.map.MapScreenModel
import org.beem.tastymap.map.rememberTastyMapState
import org.beem.tastymap.ui.detailsheet.TastyDetailSheet


class TastyMapScreen : Screen {
    @Composable
    override fun Content() {
        val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

        val mapScreenModel: MapScreenModel = koinScreenModel()

        val mapState = rememberTastyMapState()

        val userLocation by mapScreenModel.userLocation.collectAsState()

        val selectedRestaurant = mapState.selectedRestaurant

        var activeRestaurant by remember { mutableStateOf<Restaurant?>(null) }



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

                activeRestaurant?.let { restaurant ->
                    TastyDetailSheet(
                        restaurant = restaurant,
                        onDismiss = {
                            activeRestaurant = null
                            mapState.clearSelectedRestaurant()
                        }
                    )
                }

            }
        }
    }

}