package org.beem.tastymap.map.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.map.api.MapDataSource
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.model.MapResponse
import org.beem.tastymap.map.model.PlaceResult

class MapRepository(
    private val dataSource: MapDataSource
) {

    private val _places = MutableStateFlow<List<PlaceResult>>(emptyList())
    val places: StateFlow<List<PlaceResult>> = _places.asStateFlow()

    private val cachedIds = mutableSetOf<String>()

    suspend fun searchMap(request: MapRequest): ResultWrapper<MapResponse>{
        return safeApiCall {
            val response = dataSource.searchMap(request)
            updatePlaces(response.results)
            response
        }
    }

    fun getPlaceById(placeId: String): PlaceResult? {
        return _places.value.find { it.place_id == placeId }
    }

    private fun updatePlaces(newPlaces: List<PlaceResult>) {
        val filteredPlaces = newPlaces.filter { place ->
            place.place_id !in cachedIds
        }

        if(filteredPlaces.isNotEmpty()){
            cachedIds.addAll(
                filteredPlaces.map {
                    it.place_id
                }
            )
            _places.update {
                it + filteredPlaces
            }
        }
    }
}