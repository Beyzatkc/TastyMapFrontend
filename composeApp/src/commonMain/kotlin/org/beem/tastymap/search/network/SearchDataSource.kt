package org.beem.tastymap.search.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import org.beem.tastymap.core.network.DevTokenProvider
import org.beem.tastymap.search.model.SearchResponse

class SearchDataSource(
    private val httpClient: HttpClient
) {
    suspend fun searchVenues(
        searchText: String,
        page: Int = 0,
        size: Int = 20
    ): SearchResponse {
        return httpClient.get("search") {
            parameter("searchText", searchText)
            parameter("page", page)
            parameter("size", size)
            DevTokenProvider.authHeader?.let { header(HttpHeaders.Authorization, it) }
        }.body()
    }
}