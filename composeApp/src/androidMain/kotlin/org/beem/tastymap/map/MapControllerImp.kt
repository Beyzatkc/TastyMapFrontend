package org.beem.tastymap.map

import android.animation.ValueAnimator
import org.beem.tastymap.data.model.Restaurant
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.android.maps.MapView

class MapControllerImp(
    private val map: MapView,
    private val style: Style
) : MapController {

    val SOURCE_ID = "user-location-source"
    val LAYER_ID = "user-location-layer"
    val ICON_ID = "user-navigation-icon"

    private var animator: ValueAnimator? = null
    private var lastLat = 0.0
    private var lastLng = 0.0
    private var lastBearing = 0f

    override fun updateMapData(geoJson: String) {
        style.getSourceAs<GeoJsonSource>("restaurant-source")?.setGeoJson(
            FeatureCollection.fromJson(geoJson)
        )
    }

    override fun onClickMarker(onMarkerClicked: (Restaurant) -> Unit) {
        map.getMapAsync { mapLibreMap ->
            mapLibreMap.addOnMapClickListener { point ->
                val screenPoint = mapLibreMap.projection.toScreenLocation(point)

                val features = mapLibreMap.queryRenderedFeatures(screenPoint, "restaurant-layer")

                if (features.isNotEmpty()) {
                    val feature = features[0]
                    val id = feature.getStringProperty("id")
                    val name = feature.getStringProperty("name")
                    val geometry = feature.geometry() as Point

                    val restaurant = Restaurant(
                        id = id,
                        name = name,
                        longitude = geometry.longitude(),
                        latitude = geometry.latitude(),
                        cuisine = "",
                        rating = 0.0
                    )
                    onMarkerClicked(restaurant)
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun animateTo(lat: Double, lng: Double, zoom: Float) {
        val pos = CameraPosition.Builder()
            .target(LatLng(lat, lng))
            .zoom(zoom.toDouble())
            .build()
        map.getMapAsync{
            it.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 1000)
        }
    }

    override fun addMarker(lat: Double, lng: Double, title: String) {
        map.getMapAsync{
            it.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(title))
        }
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