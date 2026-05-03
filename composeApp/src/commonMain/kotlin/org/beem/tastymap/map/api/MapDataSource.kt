package org.beem.tastymap.map.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.map.model.MapRequest
import org.beem.tastymap.map.model.MapResponse

class MapDataSource (private val client: HttpClient){

    suspend fun searchMap(request: MapRequest): MapResponse {
        return client.post("places/nearby"){
            setBody(request)
        }.body<MapResponse>()
    }
}