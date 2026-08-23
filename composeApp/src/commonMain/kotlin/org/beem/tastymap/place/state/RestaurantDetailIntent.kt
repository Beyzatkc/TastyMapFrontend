package org.beem.tastymap.place.state

sealed interface RestaurantDetailIntent {
    data class QuickScoreChanged(val score: Double) : RestaurantDetailIntent
    data class OpenAddReview(val initialScore: Double) : RestaurantDetailIntent
    data object DismissAddReview : RestaurantDetailIntent
    data object ReviewSubmittedSuccess : RestaurantDetailIntent
    data object RetryDetails : RestaurantDetailIntent
    data object LoadMoreReviews : RestaurantDetailIntent
    data object DismissMainSheet : RestaurantDetailIntent
}