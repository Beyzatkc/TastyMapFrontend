package org.beem.tastymap.place.state

import org.beem.tastymap.place.model.review.ReviewItem
import org.beem.tastymap.place.model.review.UserReviewSummaryDto

sealed interface RestaurantDetailIntent {
    data class QuickScoreChanged(val score: Double) : RestaurantDetailIntent
    data class OpenAddReview(val initialScore: Double) : RestaurantDetailIntent
    data object DismissAddReview : RestaurantDetailIntent
    data object RetryDetails : RestaurantDetailIntent
    data object LoadMoreReviews : RestaurantDetailIntent
    data object DismissMainSheet : RestaurantDetailIntent

    data class ReviewCreatedLocally(
        val review: ReviewItem,
        val userSummary: UserReviewSummaryDto
    ) : RestaurantDetailIntent

    data class ReviewUpdatedLocally(
        val review: ReviewItem,
        val userSummary: UserReviewSummaryDto
    ) : RestaurantDetailIntent

    data class ReviewDeletedLocally(
        val reviewId: Long,
        val deletedScore: Double
    ) : RestaurantDetailIntent


    data object StartDirections : RestaurantDetailIntent
}