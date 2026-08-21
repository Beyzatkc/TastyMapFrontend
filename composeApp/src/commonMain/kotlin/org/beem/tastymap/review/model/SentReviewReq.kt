package org.beem.tastymap.review.model

import kotlinx.serialization.Serializable

@Serializable
data class SentReviewReq(
    val parentId: Long? = null,
    val content: String?,
    val placeId: String,
    val scores: List<ScoreDto>
)