package org.beem.tastymap.route.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.BaseResponse
import org.beem.tastymap.route.model.RouteDirectionDto

class RouteDataSource(
    private val authHttpClientManager: AuthHttpClientManager
) {
    private val client: HttpClient get() = authHttpClientManager.getClient()

    suspend fun getDirections(
        placeId: String,
        userLat: Double,
        userLng: Double,
        forceRefresh: Boolean = false
    ): BaseResponse<RouteDirectionDto> {
        return client.get("route/$placeId/directions"){
            parameter("userLat", userLat)
            parameter("userLng", userLng)
            parameter("forceRefresh", forceRefresh)
        }.body()
    }
}