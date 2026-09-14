package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.search.UserSearchResponse

class SearchUserDataSource(private val client: HttpClient) {

    suspend fun getSearchUsers(keyword: String, page: Int = 0, size: Int = 20): List<UserSearchResponse> {
        return client.get("api/search/users") {
            parameter("keyword", keyword)
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getHistorySearchUsers(): PageResponse<UserSearchResponse> {
        return client.get("api/search/history").body()
    }

    suspend fun addToHistory(clickedUserId: Long) {
        client.post("api/search/history/$clickedUserId")
    }

    suspend fun deleteFromHistory(userIdToDelete: Long) {
        client.delete("api/search/history-delete/$userIdToDelete")
    }
}