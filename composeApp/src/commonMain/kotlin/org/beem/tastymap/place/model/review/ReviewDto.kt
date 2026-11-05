package org.beem.tastymap.place.model.review

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.place.model.MapReviewSource
import org.beem.tastymap.review.model.ScoreDto

@Serializable
data class ReviewDto(
    @SerialName("review_id")
    val reviewId: Long? = null,

    val source: MapReviewSource = MapReviewSource.GOOGLE,

    @SerialName("author_name")
    val authorName: String,

    val rating: Double,
    val text: String? = null,
    val time: Long? = null,
    val scores: List<ScoreDto> = emptyList()
)