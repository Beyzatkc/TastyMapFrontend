package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.beem.tastymap.data.model.PostResponseDTO

class UserDataSource(private val client: HttpClient) {

    suspend fun getMyPosts(
        page: Int = 0,
        size: Int = 10
    ): PostResponseDTO {
        return client.get("api/posts/getMePosts") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
}