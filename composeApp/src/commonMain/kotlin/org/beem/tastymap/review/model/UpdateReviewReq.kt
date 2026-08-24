package org.beem.tastymap.review.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReviewReq(
    val reviewId: Long,
    val mainRating: Double,
    val content: String? = null,
    val scores: List<ScoreDto> = emptyList()
)