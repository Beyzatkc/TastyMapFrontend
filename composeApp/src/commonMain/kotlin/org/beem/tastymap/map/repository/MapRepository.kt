package org.beem.tastymap.map.repository

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

    // $O(1)$ Grid Havuzu (Key: "lat:lng", Value: O griddeki mekan ID listesi)
    private val gridPlaceIds = mutableMapOf<String, MutableSet<String>>()

    // Tüm mekanların tekil nesne havuzu (Key: place_id)
    private val allPlacesMap = mutableMapOf<String, PlaceResult>()
    private val allFeaturesMap = mutableMapOf<String, Feature>()

    // Taranan gridleri takip eden Set
    private val scannedGridKeys = mutableSetOf<String>()

    private val MAX_VISIBLE_PINS = 50

    suspend fun searchMap(request: MapRequest, forceRefresh: Boolean = false): ResultWrapper<MapResponse> {
        val centerGridKey = GridUtils.getGridKey(request.lat, request.lng)
        val surroundingKeys = GridUtils.getSurroundingGridKeys(request.lat, request.lng, request.radius)

        // 1. RAM KONTROLÜ: Merkez grid daha önce taranmış mı?
        if (!forceRefresh && scannedGridKeys.contains(centerGridKey)) {
            val cachedResponse = buildResponseFromGridKeys(surroundingKeys)
            if (cachedResponse.results.isNotEmpty()) {
                return ResultWrapper.Success(cachedResponse)
            }
        }

        // 2. NETWORK ÇAĞRISI: Grid bellekte yoksa backend'e sor
        return safeApiCall {
            val response = dataSource.searchMap(request)

            response.results.forEach { place ->
                allPlacesMap[place.place_id] = place

                // Mekanın ait olduğu grid anahtarını bulup grid setine ekle
                val placeLat = place.geometry?.location?.lat ?: request.lat
                val placeLng = place.geometry?.location?.lng ?: request.lng
                val placeGridKey = GridUtils.getGridKey(placeLat, placeLng)

                gridPlaceIds.getOrPut(placeGridKey) { mutableSetOf() }.add(place.place_id)
            }

            response.geoJson.features.forEach { feature ->
                allFeaturesMap[feature.properties.id] = feature
            }

            scannedGridKeys.add(centerGridKey)

            // 3x3 komşu gridlerin tamamından ekrana verilecek filtrelenmiş paketi hazırla
            buildResponseFromGridKeys(surroundingKeys, response.geoJson)
        }
    }

    /**
     * Verilen Grid anahtarlarındaki mekanları birleştirip UI formatına getirir.
     */
    private fun buildResponseFromGridKeys(
        gridKeys: List<String>,
        baseGeoJson: GeoJson? = null
    ): MapResponse {
        // İlgili gridlerdeki tüm tekil place_id'leri topla
        val targetPlaceIds = mutableSetOf<String>()
        gridKeys.forEach { key ->
            gridPlaceIds[key]?.let { targetPlaceIds.addAll(it) }
        }

        // Eğer grid eşleşmesinden az mekan çıkarsa hafızadaki tüm mekanlardan tamamla
        val targetFeatures = if (targetPlaceIds.isNotEmpty()) {
            targetPlaceIds.mapNotNull { allFeaturesMap[it] }
        } else {
            allFeaturesMap.values.toList()
        }.take(MAX_VISIBLE_PINS)

        val targetPlaces = targetFeatures.mapNotNull { allPlacesMap[it.properties.id] }

        val geoJsonResult = GeoJson(
            type = baseGeoJson?.type ?: "FeatureCollection",
            features = targetFeatures
        )

        return MapResponse(
            results = targetPlaces,
            geoJson = geoJsonResult,
            status = "Ok"
        )
    }

    fun getPlaceById(placeId: String): PlaceResult? {
        return allPlacesMap[placeId]
    }

    fun clearCache() {
        gridPlaceIds.clear()
        allPlacesMap.clear()
        allFeaturesMap.clear()
        scannedGridKeys.clear()
    }
}