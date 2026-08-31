package org.beem.tastymap.route.repository

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.route.model.RouteDirectionDto

interface RouteRepository {
    suspend fun fetchRoute(placeId: String, userLat: Double, userLng: Double): ResultWrapper<BaseResponse<RouteDirectionDto>>
}