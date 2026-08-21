package org.beem.tastymap.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.beem.tastymap.R
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.map.mapstylelayers.setupRestaurantLayer
import org.beem.tastymap.map.mapstylelayers.setupUserLocationLayer
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState,
    onFabClicked: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { mapLibreMap ->
                        mapLibreMap.setStyle(Style.Builder().fromUri(mapUrl)) { style ->
                            style.setupUserLocationLayer(
                                context = context,
                                iconId = "user-navigation-icon",
                                iconRes = android.R.drawable.ic_menu_mylocation
                            )

                            style.setupRestaurantLayer(context)

                            val controller = MapControllerImp(mapView, style)
                            state.controller = controller
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        FloatingActionButton(
            onClick = onFabClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Merkeze Odaklan"
            )
        }
    }
}