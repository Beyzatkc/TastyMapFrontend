package org.beem.tastymap.place.model.details

import org.beem.tastymap.review.model.ScoreType

data class HighlightUiModel(
    val type: ScoreType,
    val title: String,
    val subtitle: String,
    val content: String
)