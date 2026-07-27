package org.beem.tastymap.place.state

import org.beem.tastymap.place.model.review.ReviewItem

data class ReviewPagingState(
    val items: List<ReviewItem> = emptyList(),
    val page: Int = 0,
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val currentPlaceId: String? = null
)