package org.beem.tastymap.place.model.review

import kotlinx.serialization.Serializable
import org.beem.tastymap.review.model.ScoreDto

@Serializable
data class UserReviewSummaryDto(
    val reviewId: Long,
    val userName: String,
    val rating: Double,
    val text: String? = null,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val scores: List<ScoreDto> = emptyList()
)