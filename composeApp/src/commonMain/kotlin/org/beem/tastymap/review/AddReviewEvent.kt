package org.beem.tastymap.review

sealed interface AddReviewEvent {
    data object Success : AddReviewEvent
    data class Error(val message: String) : AddReviewEvent
}