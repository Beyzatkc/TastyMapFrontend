package org.beem.tastymap.review

import org.beem.tastymap.review.model.ScoreType

data class AddReviewState(
    val isEditMode: Boolean = false,
    val reviewId: Long? = null,
    val mainScore: Double = 0.0,
    val subScores: Map<ScoreType, Double> = emptyMap(),
    val comment: String = "",
    val isAdvancedExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)