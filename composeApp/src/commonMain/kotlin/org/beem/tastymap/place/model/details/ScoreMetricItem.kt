package org.beem.tastymap.place.model.details

import kotlinx.serialization.Serializable
import org.beem.tastymap.review.model.ScoreType

@Serializable
data class ScoreMetricItem(
    val type: ScoreType,
    val label: String,
    val averageScore: Double,
    val count: Int
)