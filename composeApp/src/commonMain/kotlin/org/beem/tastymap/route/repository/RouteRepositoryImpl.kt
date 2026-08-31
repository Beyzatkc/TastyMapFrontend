package org.beem.tastymap.route.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.route.model.RouteDirectionDto
import org.beem.tastymap.route.network.RouteDataSource

class RouteRepositoryImpl(
    private val source: RouteDataSource
) : RouteRepository {

    override suspend fun fetchRoute(
        placeId: String,
        userLat: Double,
        userLng: Double
    ): ResultWrapper<BaseResponse<RouteDirectionDto>> {
        val result = safeApiCall {
            source.getDirections(placeId, userLat, userLng)
        }
        return result
    }
}