package org.beem.tastymap.route.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import org.beem.tastymap.core.network.DevTokenProvider
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.route.model.RouteDirectionDto

class RouteDataSource(
    private val client: HttpClient
) {
    suspend fun getDirections(
        placeId: String,
        userLat: Double,
        userLng: Double
    ): BaseResponse<RouteDirectionDto> {
        return client.get("route/$placeId/directions"){
            parameter("userLat", userLat)
            parameter("userLng", userLng)
            DevTokenProvider.authHeader?.let { header(HttpHeaders.Authorization, it) }
        }.body()
    }
}