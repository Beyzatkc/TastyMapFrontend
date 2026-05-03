package org.beem.tastymap.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.beem.tastymap.R.drawable.navigation
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import android.animation.ValueAnimator
import org.beem.tastymap.data.model.LocationData
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.map.mapstylelayers.setupRestaurantLayer
import org.beem.tastymap.map.mapstylelayers.setupUserLocationLayer
import org.maplibre.geojson.FeatureCollection

private var lastLat = 0.0
private var lastLng = 0.0
private var lastBearing = 0f
private var animator: ValueAnimator? = null

@Composable
actual fun TastyMapComponent(
    modifier: Modifier,
    mapUrl: String,
    userLocation: LocationData,
    state: TastyMapState
) {
    val context = LocalContext.current

    val SOURCE_ID = "user-location-source"
    val LAYER_ID = "user-location-layer"
    val ICON_ID = "user-navigation-icon"


    val mapView = remember { MapView(context) }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            map.setStyle(
                Style.Builder().fromUri(
                    mapUrl
                )
            ) { style ->
                style.setupUserLocationLayer(context, "user-icon", navigation)
                style.setupRestaurantLayer(context)

                state.controller = MapControllerImp(mapView, style)
            }
        }
    }
    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
