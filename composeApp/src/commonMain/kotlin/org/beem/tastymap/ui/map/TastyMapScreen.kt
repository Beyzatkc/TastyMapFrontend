package org.beem.tastymap.ui.map


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.beem.tastymap.map.TastyMapComponent
import org.beem.tastymap.permission.LocationPermissionWrapper
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.beem.tastymap.map.MapEvent
import org.beem.tastymap.map.MapScreenModel
import org.beem.tastymap.map.rememberTastyMapState
import org.koin.compose.viewmodel.koinViewModel


class TastyMapScreen : Screen {
    @Composable
    override fun Content() {
        val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

        val mapScreenModel: MapScreenModel = koinScreenModel()
        val mapState = rememberTastyMapState()

        val userLocation by mapScreenModel.userLocation.collectAsState()


        LaunchedEffect(Unit){
            mapScreenModel.event.collect{ event ->
                when(event){
                    is MapEvent.CenterOn -> {
                        mapState.centerOn(event.lat, event.lng)
                    }
                    is MapEvent.UserMarker -> {
                        mapState.userMarker(event.lat, event.lng, event.title, event.bearing)
                    }

                    is MapEvent.UpdateMapGeoSource -> {
                        mapState.updateMapData(event.source)
                    }
                }
            }
            mapScreenModel.startObservingLocation()
        }

        mapState.onClickMarker { restaurant ->
            mapScreenModel.onMarkerClicked(restaurant)
        }


        MaterialTheme {
            LocationPermissionWrapper(
                onPermissionGranted = {
                    mapScreenModel.startObservingLocation()
                }
            ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        TastyMapComponent(
                            modifier = Modifier.fillMaxSize(),
                            mapUrl = myMaps,
                            state = mapState,
                            userLocation = userLocation,
                        )
                        //RestaurantDetailsSheet(mapScreenModel)
                        FloatingActionButton(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            onClick = {
                                mapScreenModel.onCenterMapClicked()
                                mapScreenModel.fetchNearbyRestaurants(
                                    userLocation.latitude,
                                    userLocation.longitude
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = "Merkez"
                            )
                        }
                    }
            }
        }
    }

}