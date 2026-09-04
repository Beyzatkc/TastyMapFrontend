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
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.geojson.LineString

class MapControllerImp(
    private val map: MapView,
    private val style: Style
) : MapController {

    private val ROUTE_SOURCE_ID = "tastymap-route-source"
    private val ROUTE_CASING_LAYER_ID = "tastymap-route-casing-layer"
    private val ROUTE_MAIN_LAYER_ID = "tastymap-route-main-layer"

    private val CONNECTOR_SOURCE_ID = "tastymap-connector-source"
    private val CONNECTOR_LAYER_ID = "tastymap-connector-layer"

    private val RESTAURANT_LAYER_ID = "restaurant-layer"

    val SOURCE_ID = "user-location-source"
    val LAYER_ID = "user-location-layer"

    private val SELECTED_PIN_SOURCE_ID = "tastymap-selected-pin-source"
    private val SELECTED_PIN_LAYER_ID = "tastymap-selected-pin-layer"

    private var animator: ValueAnimator? = null
    private var lastLat = 0.0
    private var lastLng = 0.0
    private var lastBearing = 0f

    private var lastZoomLevel: Double? = null

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
                val features = mapLibreMap.queryRenderedFeatures(screenPoint, RESTAURANT_LAYER_ID)

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

    override fun animateTo(lat: Double, lng: Double, zoom: Float?) {
        map.getMapAsync { mapLibreMap ->
            val targetZoom = zoom?.toDouble() ?: mapLibreMap.cameraPosition.zoom

            val pos = CameraPosition.Builder()
                .target(LatLng(lat, lng))
                .zoom(targetZoom)
                .build()

            mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 1000)
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

    override fun drawRoute(
        mainRoute: List<List<Double>>,
        startConnector: List<List<Double>>,
        endConnector: List<List<Double>>,
        targetPlaceId: String?
    ) {
        if (mainRoute.isEmpty()) return

        map.post {
            try {
                val restaurantLayer = style.getLayerAs<SymbolLayer>(RESTAURANT_LAYER_ID)
                if (restaurantLayer != null && !targetPlaceId.isNullOrBlank()) {
                    restaurantLayer.setFilter(
                        Expression.eq(Expression.get("id"), Expression.literal(targetPlaceId))
                    )
                }

                val targetBelow = when {
                    style.getLayer(RESTAURANT_LAYER_ID) != null -> RESTAURANT_LAYER_ID
                    style.getLayer(LAYER_ID) != null -> LAYER_ID
                    else -> null
                }

                // 1. NOKTALI BAĞLANTI ÇİZGİLERİ (. . . .)
                val connectorLines = mutableListOf<LineString>()
                if (startConnector.size >= 2) {
                    connectorLines.add(LineString.fromLngLats(startConnector.map { Point.fromLngLat(it[0], it[1]) }))
                }
                if (endConnector.size >= 2) {
                    connectorLines.add(LineString.fromLngLats(endConnector.map { Point.fromLngLat(it[0], it[1]) }))
                }

                val connSource = style.getSourceAs<GeoJsonSource>(CONNECTOR_SOURCE_ID)
                if (connectorLines.isNotEmpty()) {
                    val connectorFeatures = connectorLines.map { Feature.fromGeometry(it) }
                    val connectorCollection = FeatureCollection.fromFeatures(connectorFeatures.toTypedArray())

                    if (connSource != null) {
                        connSource.setGeoJson(connectorCollection)
                    } else {
                        style.addSource(GeoJsonSource(CONNECTOR_SOURCE_ID, connectorCollection))
                        val connectorLayer = LineLayer(CONNECTOR_LAYER_ID, CONNECTOR_SOURCE_ID).apply {
                            setProperties(
                                PropertyFactory.lineColor(AppColors.NavySoft.toArgb()),
                                PropertyFactory.lineWidth(4.5f),
                                // Yuvarlak uç + sıfır çizgi boyu = Kusursuz Nokta (. . . .)
                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                PropertyFactory.lineDasharray(arrayOf(0.01f, 1.8f)),
                                PropertyFactory.lineOpacity(0.9f)
                            )
                        }
                        if (targetBelow != null) style.addLayerBelow(connectorLayer, targetBelow)
                        else style.addLayer(connectorLayer)
                    }
                } else {
                    connSource?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
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

                // 3. KAMERA SINIRLARI
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

    override fun clearRoute() {
        map.post {
            try {
                val restaurantLayer = style.getLayerAs<SymbolLayer>(RESTAURANT_LAYER_ID)
                restaurantLayer?.setFilter(Expression.literal(true))
                // Hem ana rotayı hem de noktalı bağlantıları tamamen sıfırlar
                style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
                style.getSourceAs<GeoJsonSource>(CONNECTOR_SOURCE_ID)?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
            } catch (e: Exception) {
                println("TastyMap HATA -> Rota temizlenirken hata: ${e.message}")
            }
        }
    }

    override fun setOnCameraIdleListener(onCameraIdle: (centerLat: Double, centerLng: Double, zoom: Double) -> Unit) {
        map.getMapAsync { mapLibreMap ->
            mapLibreMap.addOnCameraIdleListener {
                val target = mapLibreMap.cameraPosition.target
                val zoom = mapLibreMap.cameraPosition.zoom
                if (target != null) {
                    onCameraIdle(target.latitude, target.longitude, zoom)
                }
            }
        }
    }

    override fun showSelectedPin(placeId: String, lat: Double, lng: Double) {
        map.post {
            try {
                val restaurantLayer = style.getLayerAs<SymbolLayer>(RESTAURANT_LAYER_ID)

                restaurantLayer?.setFilter(Expression.literal(true))

                if (restaurantLayer != null && placeId.isNotBlank()) {
                    // id != placeId olanlar görünür kalsın, aranan mekan GONE olsun
                    restaurantLayer.setFilter(
                        Expression.neq(Expression.get("id"), Expression.literal(placeId))
                    )
                }

                // 2. Özel Vurgu / Odak Pinini Oluştur veya Koordinatını Güncelle
                val point = Point.fromLngLat(lng, lat)
                val featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(point))
                val source = style.getSourceAs<GeoJsonSource>(SELECTED_PIN_SOURCE_ID)

                if (source != null) {
                    source.setGeoJson(featureCollection)
                } else {
                    style.addSource(GeoJsonSource(SELECTED_PIN_SOURCE_ID, featureCollection))

                    val pinLayer = SymbolLayer(SELECTED_PIN_LAYER_ID, SELECTED_PIN_SOURCE_ID).apply {
                        setProperties(
                            PropertyFactory.iconImage("tm_selected_search_pin"),

                            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconIgnorePlacement(true)
                        )
                    }
                    val restaurantLayer = style.getLayer(RESTAURANT_LAYER_ID)

                    if (restaurantLayer != null) {
                        style.addLayerAbove(pinLayer, RESTAURANT_LAYER_ID)
                    } else {
                        style.addLayer(pinLayer)
                    }
                }
            } catch (e: Exception) {
                println("TastyMap HATA -> Search Pin basılırken hata: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun clearSelectedPin() {
        map.post {
            try {
                // 1. Gizlenen orijinal mekan ikonunu geri getir (Filtreyi sıfırla)
                val restaurantLayer = style.getLayerAs<SymbolLayer>(RESTAURANT_LAYER_ID)
                restaurantLayer?.setFilter(Expression.literal(true))

                // 2. Geçici arama pinini temizle
                val source = style.getSourceAs<GeoJsonSource>(SELECTED_PIN_SOURCE_ID)
                source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
            } catch (e: Exception) {
                println("TastyMap HATA -> Search Pin temizlenirken hata: ${e.message}")
            }
        }
    }

    override fun setOnZoomChangedListener(onZoomChanged: () -> Unit) {
        map.getMapAsync { mapLibreMap ->
            mapLibreMap.addOnCameraMoveListener {
                val currentZoom = mapLibreMap.cameraPosition.zoom
                val prevZoom = lastZoomLevel

                // İlk açılışta referans zoom değerini ata
                if (prevZoom == null) {
                    lastZoomLevel = currentZoom
                    return@addOnCameraMoveListener
                }

                // Eğer zoom seviyesinde fark varsa (hassasiyet eşiği: 0.05)
                if (kotlin.math.abs(currentZoom - prevZoom) > 0.05) {
                    lastZoomLevel = currentZoom
                    onZoomChanged()
                }
            }

            // Harita durduğunda referans değeri güncelle
            mapLibreMap.addOnCameraIdleListener {
                lastZoomLevel = mapLibreMap.cameraPosition.zoom
            }
        }
    }

    override fun setOnMapClickListener(onMapClick: () -> Unit) {
        map.getMapAsync { mapLibreMap ->
            mapLibreMap.addOnMapClickListener { point ->
                val screenPoint = mapLibreMap.projection.toScreenLocation(point)
                val features = mapLibreMap.queryRenderedFeatures(screenPoint, RESTAURANT_LAYER_ID)

                if (features.isEmpty()) {
                    onMapClick()
                }
                false
            }
        }
    }

}