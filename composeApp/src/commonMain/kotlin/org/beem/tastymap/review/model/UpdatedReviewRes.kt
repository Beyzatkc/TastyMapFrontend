package org.beem.tastymap.review.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdatedReviewRes(
    val reviewId: Long,
    val placeId: Long,
    val author: String,
    val status: String,
    val createdAt: Long,
    val updateAt: Long? = null,
    val scores: List<ScoreDto> = emptyList()
)