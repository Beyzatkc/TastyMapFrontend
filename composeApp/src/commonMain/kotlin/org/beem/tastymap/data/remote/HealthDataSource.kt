package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.beem.tastymap.data.model.health.HealthRequest
import org.beem.tastymap.data.model.health.HealthResponse

class HealthDataSource(private val client: HttpClient) {

    suspend fun addHealth(request: HealthRequest): HealthResponse{
        return client.post("api/HealthInfo/addHealth"){
            setBody(request)
        }.body()
    }

    suspend fun updateHealth(request: HealthRequest): HealthResponse{
        return client.put("api/HealthInfo/updateHealth"){
            setBody(request)
        }.body()
    }

    suspend fun getHealth(): HealthResponse{
        return client.get("api/HealthInfo/getHealthInfo"){
        }.body()
    }
}