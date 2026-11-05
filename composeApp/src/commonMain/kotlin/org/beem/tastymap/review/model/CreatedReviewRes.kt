package org.beem.tastymap.review.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatedReviewRes(
    @SerialName("reviewId")
    val reviewId: Long,

    @SerialName("placeId")
    val placeId: Long,

    @SerialName("author")
    val author: String,

    @SerialName("status")
    val status: ReviewStatus,

    @SerialName("createdAt")
    val createdAt: Long,

    @SerialName("scores")
    val scores: List<ScoreDto> = emptyList()
)