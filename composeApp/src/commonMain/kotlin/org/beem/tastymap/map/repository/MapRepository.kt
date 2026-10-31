package org.beem.tastymap.map.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.util.GridUtils
import org.beem.tastymap.map.api.MapDataSource
import org.beem.tastymap.map.model.Feature
import org.beem.tastymap.map.model.GeoJson
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.model.MapResponse
import org.beem.tastymap.map.model.PlaceResult

class MapRepository(
    private val dataSource: MapDataSource
) {

    private val mutex = Mutex()

    // Grid Key -> O bölgede bulunan Place ID'leri
    private val gridPlaceIds = mutableMapOf<String, MutableSet<String>>()

    // place_id bazlı mekan ve feature havuzu
    private val allPlacesMap = mutableMapOf<String, PlaceResult>()
    private val allFeaturesMap = mutableMapOf<String, Feature>()


    private val scannedGridCacheKeys = mutableSetOf<String>()

    private val MAX_VISIBLE_PINS = 50

    suspend fun searchMap(request: MapRequest, forceRefresh: Boolean = false): ResultWrapper<MapResponse> {
        val centerGridKey = GridUtils.getGridKey(request.lat, request.lng)
        val surroundingKeys = GridUtils.getSurroundingGridKeys(request.lat, request.lng, request.radius)

        println("TastyMap Repo -> [İSTEK] Hedef Lat: ${request.lat}, Lng: ${request.lng}")
        println("TastyMap Repo -> Merkez Grid: $centerGridKey | Komşu Grid Sayısı: ${surroundingKeys.size}")

        val currentScanCacheKey = GridUtils.buildGridCacheKey(centerGridKey, request.keywords)

        val isCached = isCached(currentScanCacheKey, forceRefresh)

        if (isCached) {
            val cachedResponse = buildResponseFromGridKeys(surroundingKeys)
            println("TastyMap Repo -> Grid RAM cache'ten okundu. Dönen Mekan: ${cachedResponse.results.size}")
            return ResultWrapper.Success(cachedResponse)
        }

        // 2. NETWORK ÇAĞRISI
        return safeApiCall {
            val response = dataSource.searchMap(request)
            println("TastyMap Repo -> [BACKEND CEVAP] Gelen Mekan Sayısı: ${response.results.size}, Feature Sayısı: ${response.geoJson.features.size}")

            mutex.withLock {

                // 1. Adım: Feature'ları properties.id üzerinden güvenle kaydet
                response.geoJson.features.forEach { feature ->
                    val pId = feature.properties.id
                    if (pId.isNotBlank()) {
                        allFeaturesMap[pId] = feature
                    }
                }

                // 2. Adım: Mekanları kaydet ve grid'e bağla
                response.results.forEach { place ->
                    val placeId = place.place_id
                    allPlacesMap[placeId] = place

                    val placeLat = place.geometry?.location?.lat ?: request.lat
                    val placeLng = place.geometry?.location?.lng ?: request.lng
                    val placeGridKey = GridUtils.getGridKey(placeLat, placeLng)

                    // Mekanın kendi koordinat gridine ekle
                    gridPlaceIds.getOrPut(placeGridKey) { mutableSetOf() }.add(placeId)
                }

                scannedGridCacheKeys.add(currentScanCacheKey)
            }

            buildResponseFromGridKeys(surroundingKeys)
        }
    }

    private suspend fun buildResponseFromGridKeys(gridKeys: List<String>): MapResponse {
        return mutex.withLock {
            val targetPlaceIds = mutableSetOf<String>()
            gridKeys.forEach { key ->
                gridPlaceIds[key]?.let { targetPlaceIds.addAll(it) }
            }

            // Toplanan mekanları puana göre sıralayıp en iyi 50 tanesini al
            val targetPlaces = targetPlaceIds
                .mapNotNull { allPlacesMap[it] }
                .sortedWith(
                    compareByDescending<PlaceResult> { it.rating ?: 0.0 }
                        .thenByDescending { it.user_ratings_total ?: 0 }
                )
                .take(MAX_VISIBLE_PINS)

            // Sıralanan 50 mekanın Feature'larını topla
            val targetFeatures = targetPlaces.mapNotNull { place ->
                allFeaturesMap[place.place_id]
            }

            MapResponse(
                results = targetPlaces,
                geoJson = GeoJson(type = "FeatureCollection", features = targetFeatures),
                status = "Ok"
            )
        }
    }

    private suspend fun isCached(scanCacheKey: String, forceRefresh: Boolean = false): Boolean {
        return mutex.withLock {
            !forceRefresh && scannedGridCacheKeys.contains(scanCacheKey)
        }
    }

    suspend fun getPlaceById(placeId: String): PlaceResult? {
        return mutex.withLock {
            allPlacesMap[placeId]
        }
    }

    suspend fun clearCache() {
        mutex.withLock {
            gridPlaceIds.clear()
            allPlacesMap.clear()
            allFeaturesMap.clear()
            scannedGridCacheKeys.clear()
        }
    }
}