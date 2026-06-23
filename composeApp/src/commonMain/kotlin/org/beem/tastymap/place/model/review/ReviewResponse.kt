package org.beem.tastymap.place.model.review

import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    val page: Int,
    val size: Int,
    val reviewList: List<ReviewItem>,
    val placeId: String
)