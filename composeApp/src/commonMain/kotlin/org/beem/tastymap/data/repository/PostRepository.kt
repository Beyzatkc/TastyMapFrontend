package org.beem.tastymap.data.repository

import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.PostMemoryCache
import org.beem.tastymap.data.local.ProfileLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.post.PostAndVisitRequest
import org.beem.tastymap.data.model.post.PostGridResponse
import org.beem.tastymap.data.model.post.PostLikeResponse
import org.beem.tastymap.data.model.post.PostLikeUserResponse
import org.beem.tastymap.data.model.post.PostResponse
import org.beem.tastymap.data.model.post.PostUpdateRequest
import org.beem.tastymap.data.remote.PostDataSource

class PostRepository(
    private val dataSource: PostDataSource,
    private val memoryCache: PostMemoryCache,
    private val localDataSource: ProfileLocalDataSource,
    private val userManager: UserManager
) {
    val myId: Long
        get() = userManager.userSession.value?.userId ?: 0L

    suspend fun addPost(
        request: PostAndVisitRequest
    ): ResultWrapper<PostResponse> {
        val result = safeApiCall {
            dataSource.addPost(request)
        }
        if (result is ResultWrapper.Success) {
            memoryCache.clearUserCache(myId)
            localDataSource.incrementPostCount(myId)
        }
        return result
    }

    suspend fun getUserPosts(
        userId: Long,
        page: Int = 0,
        size: Int = 15,
        forceFetch: Boolean = false
    ): ResultWrapper<PageResponse<PostGridResponse>> {
        if (!forceFetch) {
            val cached = memoryCache.get(userId, page)
            if (cached != null) {
                return ResultWrapper.Success(cached)
            }
        }

        val result = safeApiCall {
            dataSource.getUserPosts(userId, page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(userId, page, result.data)
        }

        return result
    }

    suspend fun getMyPosts(
        page: Int = 0,
        size: Int = 15,
        forceFetch: Boolean = false
    ): ResultWrapper<PageResponse<PostGridResponse>> {
        if (!forceFetch) {
            val cached = memoryCache.get(myId, page)
            if (cached != null) {
                return ResultWrapper.Success(cached)
            }
        }

        val result = safeApiCall {
            dataSource.getMyPosts(page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(myId, page, result.data)
        }

        return result
    }


    suspend fun getPostDetail(postId: Long): ResultWrapper<PostResponse> {
        return safeApiCall {
            dataSource.getPostDetail(postId)
        }
    }

    suspend fun deletePost(postId: Long): ResultWrapper<Map<String, String>> {
        val result = safeApiCall { dataSource.deletePost(postId) }

        if (result is ResultWrapper.Success) {
            memoryCache.clearUserCache(myId)
            localDataSource.decrementPostCount(myId)
        }

        return result
    }

    suspend fun updatePost(postId: Long, request: PostUpdateRequest): ResultWrapper<Map<String, String>> {
        val result = safeApiCall { dataSource.updatePost(postId, request) }

        if (result is ResultWrapper.Success) {
            memoryCache.clearUserCache(myId)
        }

        return result
    }

    suspend fun toggleLike(postId: Long, postOwnerId: Long): ResultWrapper<PostLikeResponse> {
        val result = safeApiCall { dataSource.toggleLike(postId) }

        if (result is ResultWrapper.Success) {
            memoryCache.clearUserCache(postOwnerId)
        }

        return result
    }

    suspend fun getWhosLike(postId: Long, page: Int = 0, size: Int = 20): ResultWrapper<PageResponse<PostLikeUserResponse>> {
        return safeApiCall { dataSource.getWhosLike(postId, page, size) }
    }

    suspend fun togglePin(postId: Long): ResultWrapper<PostResponse> {
        val result = safeApiCall { dataSource.togglePin(postId) }

        if (result is ResultWrapper.Success) {
            memoryCache.clearUserCache(myId)
        }

        return result
    }
}