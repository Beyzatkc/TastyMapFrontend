package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.support.CreateSupportTicketRequest

class SupportDataSource(private val authHttpClientManager: AuthHttpClientManager) {
    private val client: HttpClient
        get() = authHttpClientManager.getClient()

    suspend fun addSupport(request: CreateSupportTicketRequest){
        client.post("api/v1/support/tickets"){
            setBody(request)
        }
    }

}