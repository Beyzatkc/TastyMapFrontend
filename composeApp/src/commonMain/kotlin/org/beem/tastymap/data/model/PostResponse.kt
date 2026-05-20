package org.beem.tastymap.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PostResponseDTO(
    val commentEnabled: Boolean,
    val postId: Long?,
    val explanation: String?,
    val puan: Int,
    val photoUrl: String?,
    val numberof_likes: Int,
    val createdAt: String?,
    val updateDate: String?,
    val userId: Long?,
    val username: String?,
    val profilePhotoUrl: String?,
    val placeId: String?,
    val placeName: String?,
    val categories: String?,
    val city: String?,
    val district: String?,
    val neighbourhood: String?,
    val latitude: Double,
    val longitude: Double,
    val averagePuan: Double,
    val isLiked: Boolean,
    val commentCount: Int,
    val isPinned: Boolean
)