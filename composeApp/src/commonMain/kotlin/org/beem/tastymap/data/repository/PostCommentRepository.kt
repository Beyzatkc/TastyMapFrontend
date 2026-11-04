package org.beem.tastymap.data.repository

import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.data.cache.PostCommentMemoryCache
import org.beem.tastymap.data.local.PostLocalDataSource
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.postcomment.CommentLikeResponse
import org.beem.tastymap.data.model.postcomment.CommentRequest
import org.beem.tastymap.data.model.postcomment.CommentsResponse
import org.beem.tastymap.data.remote.PostCommentDataSource

class PostCommentRepository(
    private val remoteDataSource: PostCommentDataSource,
    private val postLocalDataSource: PostLocalDataSource,
    private val memoryCache: PostCommentMemoryCache,
    private val userManager: UserManager
) {

    val myId: Long
        get() = userManager.userSession.value?.userId ?: 0L


    fun isMe(userId: Long): Boolean {
        return userId == myId
    }
    suspend fun addComment(postId: Long, request: CommentRequest): ResultWrapper<CommentsResponse>{
        val result = safeApiCall {
            remoteDataSource.addComment(postId, request)
        }
        if (result is ResultWrapper.Success) {
            memoryCache.invalidatePostCache(postId)
            postLocalDataSource.incrementCommentCount(postId)
        }
        return result
    }

    suspend fun addReply(postId: Long, request: CommentRequest, parentCommentId: Long): ResultWrapper<CommentsResponse>{
        val result = safeApiCall {
            remoteDataSource.addReply(postId, request, parentCommentId)
        }
        if (result is ResultWrapper.Success) {
            memoryCache.invalidatePostCache(postId)
        }
        return result
    }

    suspend fun getComments(postId: Long, page: Int = 0, size: Int = 30): ResultWrapper<PageResponse<CommentsResponse>> {
        val cachedResponse = memoryCache.get(postId, page)
        if (cachedResponse != null) {
            return ResultWrapper.Success(cachedResponse)
        }

        val result = safeApiCall {
            remoteDataSource.getComment(postId, page, size)
        }

        if (result is ResultWrapper.Success) {
            memoryCache.put(postId, page, result.data)
        }

        return result
    }

    suspend fun getReply(postId: Long, parentCommentId: Long, page: Int = 0, size: Int = 30): ResultWrapper<PageResponse<CommentsResponse>> {
        return safeApiCall {
            remoteDataSource.getReply(postId, parentCommentId, page, size)
        }
    }

    suspend fun deleteComment(postId: Long, commentId: Long): ResultWrapper<Map<String, String>> {
        val result = safeApiCall {
            remoteDataSource.deleteComment(postId, commentId)
        }
        if (result is ResultWrapper.Success) {
            postLocalDataSource.decrementCommentCount(postId)
            memoryCache.invalidatePostCache(postId)
        }
        return result
    }

    suspend fun updateComment(commentId: Long, request: CommentRequest, postId:Long): ResultWrapper<CommentsResponse> {
        val result= safeApiCall {
            remoteDataSource.updateComment(commentId,request)
        }
        if (result is ResultWrapper.Success) {
            memoryCache.invalidatePostCache(postId)
        }
        return result
    }

    suspend fun toggleLike(commentId: Long,postId: Long): ResultWrapper<CommentLikeResponse> {
        val result = safeApiCall {
            remoteDataSource.toggleLike(commentId)
        }
        if (result is ResultWrapper.Success) {
            memoryCache.invalidatePostCache(postId)
        }
        return result
    }

    suspend fun togglePin(commentId: Long, postId: Long): ResultWrapper<CommentsResponse> {
        val result = safeApiCall {
            remoteDataSource.togglePin(commentId, postId)
        }
        if (result is ResultWrapper.Success) {
            memoryCache.invalidatePostCache(postId)
        }
        return result
    }
}