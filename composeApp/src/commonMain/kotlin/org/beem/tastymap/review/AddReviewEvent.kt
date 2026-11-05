package org.beem.tastymap.review

import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.UserReviewSummaryDto

sealed interface AddReviewEvent {
    data class Created(
        val review: ReviewItem,
        val userSummary: UserReviewSummaryDto
    ) : AddReviewEvent

    data class Updated(
        val review: ReviewItem,
        val userSummary: UserReviewSummaryDto
    ) : AddReviewEvent

    data class Deleted(
        val reviewId: Long,
        val deletedScore: Double
    ) : AddReviewEvent

    data class Error(val message: String) : AddReviewEvent
}