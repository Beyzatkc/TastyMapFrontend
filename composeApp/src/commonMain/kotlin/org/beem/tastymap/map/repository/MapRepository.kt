package org.beem.tastymap.map.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.map.api.MapDataSource
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.model.MapResponse

class MapRepository(
    private val dataSource: MapDataSource
) {
    suspend fun searchMap(request: MapRequest): ResultWrapper<MapResponse>{
        return safeApiCall {
            val response = dataSource.searchMap(request)
            println(response)
            response
        }
    }
}