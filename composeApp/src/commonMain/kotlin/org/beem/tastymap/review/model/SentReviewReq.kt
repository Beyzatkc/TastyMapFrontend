package org.beem.tastymap.review.model

import kotlinx.serialization.Serializable

@Serializable
data class SentReviewReq(
    val placeId: String,
    val mainRating: Double,
    val content: String? = null,
    val parentId: Long? = null,
    val scores: List<ScoreDto> = emptyList()
)