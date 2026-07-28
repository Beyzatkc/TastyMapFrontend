package org.beem.tastymap.ui.map


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.beem.tastymap.map.TastyMapComponent
import org.beem.tastymap.permission.LocationPermissionWrapper
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.beem.tastymap.core.paging.TastyPagingState
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.MapEvent
import org.beem.tastymap.map.MapScreenModel
import org.beem.tastymap.map.rememberTastyMapState
import org.beem.tastymap.place.RestaurantDetailScreenModel
import org.beem.tastymap.ui.detailsheet.TastyDetailSheet


class TastyMapScreen : Screen {
    @Composable
    override fun Content() {
        val myMaps = "https://api.maptiler.com/maps/019dbfbf-86a2-7d38-869e-bd6ebbcee298/style.json?key=DNr5GYdtJfA7ecaMmrh1"

        val mapScreenModel: MapScreenModel = koinScreenModel()
        val detailScreenModel: RestaurantDetailScreenModel = koinScreenModel()

        val mapState = rememberTastyMapState()

        val userLocation by mapScreenModel.userLocation.collectAsState()

        val selectedRestaurant = mapState.selectedRestaurant

        var showBottomSheet by remember { mutableStateOf(false) }
        var activeRestaurant by remember { mutableStateOf<Restaurant?>(null) }

        val reviewsPagingState by detailScreenModel.reviewsPagingState.collectAsState()


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
                        showBottomSheet = true
                        activeRestaurant = event.restaurant
                    }
                }
            }
            mapScreenModel.startObservingLocation()
        }

        LaunchedEffect(activeRestaurant?.id) {
            activeRestaurant?.let {
                println("active restaurant load reviews - ${it.id}")
                detailScreenModel.loadReviewsForPlace(it.id)
            }
        }

        LaunchedEffect(selectedRestaurant) {
            selectedRestaurant?.let {
                mapScreenModel.onMarkerClicked(it)
            }
        }

        LocationPermissionWrapper(
            onPermissionGranted = {
                mapScreenModel.startObservingLocation()
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()
            ) {
                SideEffect {
                    println("Atlas UI State Kontrolü -> Eleman Sayısı: ${reviewsPagingState.items.size}, Yükleniyor: ${reviewsPagingState.isLoading}")
                }
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

                if (showBottomSheet && activeRestaurant != null) {
                    TastyDetailSheet(
                        restaurant = activeRestaurant!!,
                        pagingState = reviewsPagingState,
                        onLoadMoreReviews = {
                            println("loadMore run")
                            detailScreenModel.loadMoreReviews()
                        },
                        onDismiss = {
                            showBottomSheet = false
                            activeRestaurant = null
                            detailScreenModel.resetState()
                        },
                        onRender = {

                        }
                    )
                }
            }
        }
    }

}