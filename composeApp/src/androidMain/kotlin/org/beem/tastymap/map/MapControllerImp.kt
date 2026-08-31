package org.beem.tastymap.map

import android.animation.ValueAnimator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.beem.tastymap.data.model.Restaurant
import org.beem.tastymap.ui.theme.AppColors
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.Style
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.geojson.LineString
import java.lang.System.setProperties

class MapControllerImp(
    private val map: MapView,
    private val style: Style
) : MapController {

    private val ROUTE_SOURCE_ID = "tastymap-route-source"
    private val ROUTE_LAYER_ID = "tastymap-route-layer"

    private val ROUTE_CASING_LAYER_ID = "tastymap-route-casing-layer" // Dış beyaz hat
    private val ROUTE_MAIN_LAYER_ID = "tastymap-route-main-layer"

    private val CONNECTOR_SOURCE_ID = "tastymap-connector-source"
    private val CONNECTOR_LAYER_ID = "tastymap-connector-layer"

    val SOURCE_ID = "user-location-source"
    val LAYER_ID = "user-location-layer"
    val ICON_ID = "user-navigation-icon"

    private var animator: ValueAnimator? = null
    private var lastLat = 0.0
    private var lastLng = 0.0
    private var lastBearing = 0f

    override fun updateMapData(geoJson: String) {
        println("TastyMap -> updateMapData çağrıldı. Gelen GeoJSON boyutu: ${geoJson.length}")
        println("TastyMap -> Gelen GeoJSON içeriği: $geoJson")

        map.post {
            try {
                val source = style.getSourceAs<GeoJsonSource>("restaurant-source")
                if (source != null) {
                    val featureCollection = FeatureCollection.fromJson(geoJson)
                    println("TastyMap -> Parse edilen Feature sayısı: ${featureCollection.features()?.size ?: 0}")
                    source.setGeoJson(featureCollection)
                    println("TastyMap -> Source başarıyla güncellendi!")
                } else {
                    println("TastyMap HATA -> 'restaurant-source' henüz stilde bulunamadı!")
                }
            } catch (e: Exception) {
                println("TastyMap HATA -> GeoJSON basılırken hata oluştu: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun setupRestaurantMarkerClickListener(onRestaurantSelected: (restaurant: Restaurant) -> Unit) {
        map.getMapAsync { mapLibreMap ->
            mapLibreMap.addOnMapClickListener { point ->
                val screenPoint = mapLibreMap.projection.toScreenLocation(point)
                val features = mapLibreMap.queryRenderedFeatures(screenPoint, "restaurant-layer")

                if (features.isNotEmpty()) {
                    val feature = features[0]
                    val id = feature.getStringProperty("id") ?: ""
                    val name = feature.getStringProperty("name") ?: ""
                    val geometry = feature.geometry() as Point

                    val restaurant = Restaurant(
                        id = id,
                        name = name,
                        longitude = geometry.longitude(),
                        latitude = geometry.latitude(),
                        rating = 0.0,
                        totalRatings = 0,
                        address = "",
                        status = "",
                        category = ""
                    )
                    onRestaurantSelected(restaurant)
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

    fun drawRoute2(coordinates: List<List<Double>>) {
        if (coordinates.isEmpty()) return

        map.post {
            try {
                // 1. Koordinatları Point listesine dönüştür ([0]=lng, [1]=lat)
                val points = coordinates.map { coord ->
                    Point.fromLngLat(coord[0], coord[1])
                }
                val lineString = LineString.fromLngLats(points)
                val feature = Feature.fromGeometry(lineString)
                val featureCollection = FeatureCollection.fromFeature(feature)

                // 2. Source kontrolü / güncellemesi
                val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
                if (source != null) {
                    source.setGeoJson(featureCollection)
                } else {
                    val geoJsonSource = GeoJsonSource(ROUTE_SOURCE_ID, featureCollection)
                    style.addSource(geoJsonSource)

                    // 3. LineLayer ekle (AppColors.NavyBlue renginde)
                    val lineLayer = LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID).apply {
                        setProperties(
                            PropertyFactory.lineColor(AppColors.NavyBlue.toArgb()),
                            PropertyFactory.lineWidth(6f),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineOpacity(0.9f)
                        )
                    }

                    // Varsa restoran ikon katmanının altına, yoksa en üste ekle
                    if (style.getLayer("restaurant-layer") != null) {
                        style.addLayerBelow(lineLayer, "restaurant-layer")
                    } else {
                        style.addLayer(lineLayer)
                    }
                }

                // 4. Kamerayı rota sınırlarına sığdır (fitBounds)
                val boundsBuilder = LatLngBounds.Builder()
                coordinates.forEach { coord ->
                    boundsBuilder.include(LatLng(coord[1], coord[0]))
                }

                map.getMapAsync { mapLibreMap ->
                    try {
                        val bounds = boundsBuilder.build()
                        // 150px padding ile rotayı ekrana tam oturt
                        mapLibreMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, 150),
                            1200
                        )
                    } catch (e: Exception) {
                        println("TastyMap -> Bounds animasyon hatası: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                println("TastyMap HATA -> Rota çizilirken hata: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun drawRoute(
        mainRoute: List<List<Double>>,
        startConnector: List<List<Double>>,
        endConnector: List<List<Double>>
    ) {
        if (mainRoute.isEmpty()) return

        map.post {
            try {
                val targetBelow = when {
                    style.getLayer("restaurant-layer") != null -> "restaurant-layer"
                    style.getLayer(LAYER_ID) != null -> LAYER_ID
                    else -> null
                }

                // 1. KESİKLİ BAĞLANTILAR (-- -- --)
                val connectorLines = mutableListOf<LineString>()
                if (startConnector.size >= 2) {
                    connectorLines.add(LineString.fromLngLats(startConnector.map { Point.fromLngLat(it[0], it[1]) }))
                }
                if (endConnector.size >= 2) {
                    connectorLines.add(LineString.fromLngLats(endConnector.map { Point.fromLngLat(it[0], it[1]) }))
                }

                if (connectorLines.isNotEmpty()) {
                    val connectorFeatures = connectorLines.map { Feature.fromGeometry(it) }
                    val connectorCollection = FeatureCollection.fromFeatures(connectorFeatures.toTypedArray())

                    val connSource = style.getSourceAs<GeoJsonSource>(CONNECTOR_SOURCE_ID)
                    if (connSource != null) {
                        connSource.setGeoJson(connectorCollection)
                    } else {
                        style.addSource(GeoJsonSource(CONNECTOR_SOURCE_ID, connectorCollection))
                        val connectorLayer = LineLayer(CONNECTOR_LAYER_ID, CONNECTOR_SOURCE_ID).apply {
                            setProperties(
                                PropertyFactory.lineColor(AppColors.NavySoft.toArgb()),
                                PropertyFactory.lineWidth(3.5f),
                                PropertyFactory.lineDasharray(arrayOf(1.5f, 2.0f)),
                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineOpacity(0.85f)
                            )
                        }
                        if (targetBelow != null) style.addLayerBelow(connectorLayer, targetBelow)
                        else style.addLayer(connectorLayer)
                    }
                }

                // 2. ANA TURUNCU DÜZ ROTA
                val routePoints = mainRoute.map { Point.fromLngLat(it[0], it[1]) }
                val routeCollection = FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromLngLats(routePoints)))

                val routeSource = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
                if (routeSource != null) {
                    routeSource.setGeoJson(routeCollection)
                } else {
                    style.addSource(GeoJsonSource(ROUTE_SOURCE_ID, routeCollection))

                    val casingLayer = LineLayer(ROUTE_CASING_LAYER_ID, ROUTE_SOURCE_ID).apply {
                        setProperties(
                            PropertyFactory.lineColor(Color.White.toArgb()),
                            PropertyFactory.lineWidth(8.5f),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineOpacity(1.0f)
                        )
                    }

                    val mainLayer = LineLayer(ROUTE_MAIN_LAYER_ID, ROUTE_SOURCE_ID).apply {
                        setProperties(
                            PropertyFactory.lineColor(AppColors.GourmetOrange.toArgb()),
                            PropertyFactory.lineWidth(5.0f),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineOpacity(1.0f)
                        )
                    }

                    if (targetBelow != null) {
                        style.addLayerBelow(casingLayer, targetBelow)
                        style.addLayerBelow(mainLayer, targetBelow)
                    } else {
                        style.addLayer(casingLayer)
                        style.addLayer(mainLayer)
                    }
                }

                // 3. KAMERA SINIRLARI (Tüm noktaları kapsayacak şekilde)
                val boundsBuilder = LatLngBounds.Builder()
                startConnector.forEach { boundsBuilder.include(LatLng(it[1], it[0])) }
                mainRoute.forEach { boundsBuilder.include(LatLng(it[1], it[0])) }
                endConnector.forEach { boundsBuilder.include(LatLng(it[1], it[0])) }

                map.getMapAsync { mapLibreMap ->
                    try {
                        mapLibreMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 140),
                            1200
                        )
                    } catch (e: Exception) {
                        println("TastyMap -> Bounds animasyon hatası: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println("TastyMap HATA -> Rota çizilirken hata: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun drawRoute3(coordinates: List<List<Double>>) {
        if (coordinates.isEmpty()) return

        map.post {
            try {
                // 1. Koordinatları Point listesine dönüştür ([0]=lng, [1]=lat)
                val points = coordinates.map { coord ->
                    Point.fromLngLat(coord[0], coord[1])
                }
                val lineString = LineString.fromLngLats(points)
                val feature = Feature.fromGeometry(lineString)
                val featureCollection = FeatureCollection.fromFeature(feature)

                // 2. Source kontrolü / güncellemesi
                val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
                if (source != null) {
                    source.setGeoJson(featureCollection)
                } else {
                    val geoJsonSource = GeoJsonSource(ROUTE_SOURCE_ID, featureCollection)
                    style.addSource(geoJsonSource)

                    // 3. Alt Katman: Dış Beyaz Kontur (Casing Layer)
                    val casingLayer = LineLayer(ROUTE_CASING_LAYER_ID, ROUTE_SOURCE_ID).apply {
                        setProperties(
                            PropertyFactory.lineColor(Color.White.toArgb()),
                            PropertyFactory.lineWidth(8.5f),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineOpacity(1.0f)
                        )
                    }

                    // 4. Üst Katman: Canlı Turuncu Rota (Main Route Layer)
                    val mainLayer = LineLayer(ROUTE_MAIN_LAYER_ID, ROUTE_SOURCE_ID).apply {
                        setProperties(
                            PropertyFactory.lineColor(AppColors.GourmetOrange.toArgb()),
                            PropertyFactory.lineWidth(5.0f),
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineOpacity(1.0f)
                        )
                    }

                    // Katmanları pinlerin ve kullanıcı ikonunun altına yerleştir
                    val targetLayerBelow = when {
                        style.getLayer("restaurant-layer") != null -> "restaurant-layer"
                        style.getLayer(LAYER_ID) != null -> LAYER_ID
                        else -> null
                    }

                    if (targetLayerBelow != null) {
                        style.addLayerBelow(casingLayer, targetLayerBelow)
                        style.addLayerBelow(mainLayer, targetLayerBelow)
                    } else {
                        style.addLayer(casingLayer)
                        style.addLayer(mainLayer)
                    }
                }

                // 5. Kamerayı rota sınırlarına sığdır (fitBounds)
                val boundsBuilder = LatLngBounds.Builder()
                coordinates.forEach { coord ->
                    boundsBuilder.include(LatLng(coord[1], coord[0]))
                }

                map.getMapAsync { mapLibreMap ->
                    try {
                        val bounds = boundsBuilder.build()
                        // 140px padding ile rotayı ekrana tam oturt
                        mapLibreMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, 140),
                            1200
                        )
                    } catch (e: Exception) {
                        println("TastyMap -> Bounds animasyon hatası: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                println("TastyMap HATA -> Rota çizilirken hata: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun clearRoute() {
        map.post {
            try {
                val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
                source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
            } catch (e: Exception) {
                println("TastyMap HATA -> Rota temizlenirken hata: ${e.message}")
            }
        }
    }
}