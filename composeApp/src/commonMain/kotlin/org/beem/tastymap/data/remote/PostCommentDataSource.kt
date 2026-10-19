package org.beem.tastymap.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.beem.tastymap.core.network.AuthHttpClientManager
import org.beem.tastymap.data.model.PageResponse
import org.beem.tastymap.data.model.postcomment.CommentLikeResponse
import org.beem.tastymap.data.model.postcomment.CommentRequest
import org.beem.tastymap.data.model.postcomment.CommentsResponse

class PostCommentDataSource(private val authHttpClientManager: AuthHttpClientManager){
    private val client: HttpClient
        get() = authHttpClientManager.getClient()

    suspend fun addComment(postId: Long,request: CommentRequest): CommentsResponse {
        return client.post("api/comments/add-comments/$postId"){
            setBody(request)
        }.body()
    }

    suspend fun addReply(postId: Long, request: CommentRequest, parentCommentId: Long): CommentsResponse {
        return client.post("api/comments/add-reply/$postId/$parentCommentId"){
            setBody(request)
        }.body()
    }
    suspend fun getComment(postId: Long, page:Int = 0, size: Int = 30): PageResponse<CommentsResponse> {
        return client.get("api/comments/get-comments/$postId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
    suspend fun getReply(postId: Long,parentCommentId:Long, page:Int = 0, size: Int = 30): PageResponse<CommentsResponse> {
        return client.get("api/comments/get-replys/$postId/$parentCommentId") {
            parameter("page", page)
            parameter("size", size)
        }.body()
    }
    suspend fun deleteComment(postId: Long, commentId:Long): Map<String, String>{
        return client.delete("api/comments/delete-comment/$postId/$commentId").body()
    }

    suspend fun updateComment(commentId: Long, request:CommentRequest): CommentsResponse{
        return client.put("api/comments/update-comments/$commentId"){
            setBody(request)
        }.body()
    }

    suspend fun toggleLike(commentId: Long): CommentLikeResponse{
        return client.post("api/comments/toggle-like/$commentId").body()
    }

    suspend fun togglePin(commentId: Long,postId: Long): CommentsResponse{
        return client.put("api/comments/toggle-pin/$postId/$commentId").body()
    }




}