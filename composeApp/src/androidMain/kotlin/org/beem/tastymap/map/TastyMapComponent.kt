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
import org.maplibre.geojson.FeatureCollection
import setupRestaurantLayer
import setupUserLocationLayer


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
                style.setupRestaurantLayer(context, "res-icon", navigation)

                state.controller = object : MapController {

                    override fun setRestaurants(restaurants: List<Restaurant>) {
                        val features = restaurants.map { res ->
                            Feature.fromGeometry(Point.fromLngLat(res.longitude, res.latitude)).apply {
                                addStringProperty("id", res.id)
                                addStringProperty("name", res.name)
                            }
                        }
                        println(features)
                        style.getSourceAs<GeoJsonSource>("restaurant-source")?.setGeoJson(
                            FeatureCollection.fromFeatures(features)
                        )
                    }

                    override fun animateTo(lat: Double, lng: Double, zoom: Float) {
                        val pos = CameraPosition.Builder()
                            .target(LatLng(lat, lng))
                            .zoom(zoom.toDouble())
                            .build()
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 1000)
                    }
                    override fun addMarker(lat: Double, lng: Double, title: String) {
                        map.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(title))
                    }
                    override fun userMarker(lat: Double, lng: Double, title: String, bearing: Float) {

                        animator?.cancel()

                        val startLat = if (lastLat == 0.0) lat else lastLat
                        val startLng = if (lastLng == 0.0) lng else lastLng
                        val startBearing = lastBearing

                        animator = ValueAnimator.ofFloat(0f, 1f).apply {
                            duration = 1000
                            interpolator = android.view.animation.LinearInterpolator()

                            addUpdateListener { valAnim ->
                                val fraction = valAnim.animatedValue as Float

                                val currentLat = startLat + (lat - startLat) * fraction
                                val currentLng = startLng + (lng - startLng) * fraction
                                val currentBearing = startBearing + (bearing - startBearing) * fraction

                                val point = Point.fromLngLat(currentLng, currentLat)
                                val feature = Feature.fromGeometry(point)
                                feature.addNumberProperty("bearing", currentBearing)

                                style.getSourceAs<GeoJsonSource>(SOURCE_ID)?.setGeoJson(feature)

                                lastLat = currentLat
                                lastLng = currentLng
                                lastBearing = currentBearing
                            }
                            start()
                        }
                    }
                }
            }
        }
    }
    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
