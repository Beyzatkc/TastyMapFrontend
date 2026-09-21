package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.data.model.post.PostLikeResponse
import org.beem.tastymap.data.model.post.PostLikeUserResponse
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.data.model.post.PostUpdateRequest

class PostDataSource(private val authHttpClientManager: AuthHttpClientManager) {
    private val client: HttpClient
        get() = authHttpClientManager.getClient()

    suspend fun addPost(request: PostAndVisitRequest): PostResponse {
        return client.post("api/posts/add") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getUserPosts(userId: Long, page: Int = 0, size: Int = 15): PageResponse<PostGridResponse> {
        return client.get("api/posts/get-user-posts/$userId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getMyPosts(page: Int = 0, size: Int = 15): PageResponse<PostGridResponse> {
        return client.get("api/posts/get-me-posts") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getPostDetail(postId: Long): PostResponse {
        return client.get("api/posts/$postId").body()
    }

    suspend fun deletePost(postId: Long): Map<String, String> {
        return client.delete("api/posts/delete-post/$postId").body()
    }

    suspend fun updatePost(postId: Long, request: PostUpdateRequest): Map<String, String> {
        return client.patch("api/posts/update-post/$postId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun toggleLike(postId: Long): PostLikeResponse {
        return client.post("api/posts/toggle-like/$postId").body()
    }

    suspend fun getWhosLike(postId: Long, page: Int = 0, size: Int = 20): PageResponse<PostLikeUserResponse> {
        return client.get("api/posts/whos-like/$postId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun togglePin(postId: Long): PostResponse {
        return client.put("api/posts/toggle-pin/$postId").body()
    }
}