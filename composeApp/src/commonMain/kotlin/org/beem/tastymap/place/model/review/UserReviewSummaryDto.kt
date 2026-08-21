package org.beem.tastymap.place.model.review

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.review.model.ScoreDto

@Serializable
data class UserReviewSummaryDto(
    @SerialName("review_id")
    val reviewId: Long,

    @SerialName("author_name")
    val authorName: String,

    @SerialName("rating")
    val rating: Double,

    @SerialName("text")
    val text: String? = null,

    @SerialName("created_at")
    val createdAt: Long,

    @SerialName("scores")
    val scores: List<ScoreDto> = emptyList()
)