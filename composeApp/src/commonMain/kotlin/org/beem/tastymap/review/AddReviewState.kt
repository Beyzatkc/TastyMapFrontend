package org.beem.tastymap.review

import org.beem.tastymap.review.model.ScoreType

data class AddReviewState(
    val mainScore: Double = 5.0,
    val comment: String = "",
    val subScores: Map<ScoreType, Double> = emptyMap(),
    val isAdvancedExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)