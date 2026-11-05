package org.beem.tastymap.search.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.search.model.SearchResponse

class SearchDataSource(
    private val authHttpClientManager: AuthHttpClientManager
) {
    private val client: HttpClient get() = authHttpClientManager.getClient()

    suspend fun searchVenues(
        searchText: String,
        page: Int = 0,
        size: Int = 20
    ): SearchResponse {
        return client.get("search") {
            parameter("searchText", searchText)
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
}