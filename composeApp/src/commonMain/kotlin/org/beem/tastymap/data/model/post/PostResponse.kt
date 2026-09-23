package org.beem.tastymap.data.model.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.data.model.visit.BaseVisitRequest
import org.beem.tastymap.domain.model.RelationStatus

@Serializable
data class PostResponse(
    @SerialName("commentEnabled")
    val isCommentEnabled: Boolean = false,

    @SerialName("postId")
    val postId: Long,

    @SerialName("explanation")
    val explanation: String? = null,

    @SerialName("point")
    val point: Int = 0,

    @SerialName("photoUrl")
    val photoUrls: List<String>,

    @SerialName("numberof_likes")
    val numberOfLikes: Int = 0,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updateDate")
    val updateDate: String? = null,

    @SerialName("userId")
    val userId: Long,

    @SerialName("username")
    val username: String? = null,

    @SerialName("profilePhotoUrl")
    val profilePhotoUrl: String? = null,

    @SerialName("placeId")
    val placeId: String? = null,

    @SerialName("placeName")
    val placeName: String? = null,

    @SerialName("categories")
    val categories: String? = null,

    @SerialName("city")
    val city: String? = null,

    @SerialName("district")
    val district: String? = null,

    @SerialName("neighbourhood")
    val neighbourhood: String? = null,

    @SerialName("latitude")
    val latitude: Double = 0.0,

    @SerialName("longitude")
    val longitude: Double = 0.0,

    @SerialName("averagePoint")
    val averagePoint: Double = 0.0,

    @SerialName("isLiked")
    val isLiked: Boolean = false,

    @SerialName("commentCount")
    val commentCount: Int = 0,

    @SerialName("isPinned")
    val isPinned: Boolean = false
)
@Serializable
data class PostAndVisitRequest(
    override val placeId: String,
    override val placeName: String,
    override val categories: String? = null,
    override val city: String? = null,
    override val district: String? = null,
    override val neighbourhood: String? = null,
    override val latitude: Double,
    override val longitude: Double,
    override val averagePoint: Double? = null,
    override val isWantToPost: Boolean = false,

    val explanation: String? = null,
    val photoUrl: List<String>,
    val commentEnabled: Boolean = true
) : BaseVisitRequest

@Serializable
data class PostUpdateRequest(
    @SerialName("explanation")
    val explanation: String? = null,

    @SerialName("photoUrl")
    val photoUrl: List<String>? = null,
)

@Serializable
data class PostLikeResponse(
    @SerialName("isLiked")
    val liked: Boolean,

    @SerialName("likeCount")
    val totalLikes: Int
)

@Serializable
data class PostLikeUserResponse(
    @SerialName("userId")
    val userId: Long,

    @SerialName("username")
    val username: String,
    @SerialName("profile")
    val profile: String? = null,
    @SerialName("relationStatus")
    val relationStatus: RelationStatus? = null
)

@Serializable
data class PostGridResponse(
    val postId: Long,
    val photoUrl: String,
    @SerialName("isPinned")
    val isPinned: Boolean
)