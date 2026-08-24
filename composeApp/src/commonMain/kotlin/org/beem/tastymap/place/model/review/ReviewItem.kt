package org.beem.tastymap.place.model.review

import kotlinx.serialization.Serializable
import org.beem.tastymap.place.model.MapReviewSource
import org.beem.tastymap.review.model.ScoreDto

@Serializable
data class ReviewItem(
    val id: Long,
    val userId: Long? = null,
    val name: String,
    val userProfile: String? = null,
    val rating: Double,
    val content: String? = null,
    val source: MapReviewSource = MapReviewSource.GOOGLE,
    val likeCount: Int = 0,
    val createdAt: Long,
    val updateAt: Long? = null,
    val parentId: Long? = null,
    val scores: List<ScoreDto> = emptyList()
)