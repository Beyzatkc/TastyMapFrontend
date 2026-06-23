package org.beem.tastymap.place.model.review

import kotlinx.serialization.Serializable
import org.beem.tastymap.place.model.MapReviewSource

@Serializable
data class ReviewItem(
    val id: Long,
    val name: String,
    val rating: Double,
    val content: String,
    val source: MapReviewSource,
    val likeCount: Int,
    val createdAt: Long
)