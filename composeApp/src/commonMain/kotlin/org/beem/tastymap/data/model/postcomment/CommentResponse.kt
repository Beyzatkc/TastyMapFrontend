package org.beem.tastymap.data.model.postcomment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentRequest(
    val contents: String,
)

@Serializable
data class CommentsResponse(
    val id: Long,
    val parentCommentId: Long?,
    val replyCount: Int = 0,
    @SerialName("post_id")
    val postId: Long,
    val contents: String,
    val date: String,
    val updateDate: String?,
    val numberOfLikes: Int,
    val userId: Long,
    val username: String,
    val profilePhotoUrl: String?,
    @SerialName("liked")
    val isLiked: Boolean,
    @SerialName("pinned")
    val isPinned: Boolean
)

@Serializable
data class CommentLikeResponse(
    @SerialName("liked")
    val liked: Boolean,

    @SerialName("totalLikes")
    val totalLikes: Long = 0L
)