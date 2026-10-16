package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.visit.VisitRequest
import org.beem.tastymap.data.model.visit.VisitResponse

class VisitDataSource(private val authHttpClientManager: AuthHttpClientManager) {
    private val client: HttpClient
        get() = authHttpClientManager.getClient()

    suspend fun createVisitAction(request: VisitRequest): VisitResponse {
        return client.post("api/visits/save-visit") {
            setBody(request)
        }.body()
    }
    suspend fun getVisit(page: Int = 0, size: Int = 20): PageResponse<VisitResponse>{
        return client.get("api/visits/get-visit"){
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun deleteVisit(visitId: Long){
        return client.patch("api/visits/delete-visit/$visitId").body()
    }
}
