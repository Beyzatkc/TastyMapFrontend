package org.beem.tastymap.ui.map


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

        val restaurants by mapScreenModel.restaurants.collectAsState()
        val userLocation by mapScreenModel.userLocation.collectAsState()

        LaunchedEffect(Unit){
            mapScreenModel.event.collect{ event ->
                when(event){
                    is MapEvent.CenterOn -> {
                        mapState.centerOn(event.lat, event.lng)
                        mapState.updateRestaurants(restaurants)
                    }
                    is MapEvent.UserMarker -> {
                        mapState.userMarker(event.lat, event.lng, event.title, event.bearing)
                    }
                }
            }
            mapScreenModel.startObservingLocation()
        }

        LaunchedEffect(restaurants) {
            if (restaurants.isNotEmpty()) {
                mapState.updateRestaurants(restaurants)
            }
        }

        MaterialTheme {
            LocationPermissionWrapper(
                onPermissionGranted = {
                    mapScreenModel.startObservingLocation()
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        TastyMapComponent(
                            modifier = Modifier.fillMaxSize(),
                            mapUrl = myMaps,
                            state = mapState,
                            userLocation = userLocation,
                        )
                        FloatingActionButton(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            onClick = {
                                mapScreenModel.onCenterMapClicked()
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

}