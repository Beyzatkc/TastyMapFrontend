package org.beem.tastymap.place.model.details

import kotlinx.serialization.Serializable

@Serializable
data class PlaceStatsResult(
    val overallRating: Double = 0.0,
    val totalReviewCount: Int = 0,
    val starDistribution: Map<Int, Int> = emptyMap(),
    val criteriaMetrics: List<ScoreMetricItem> = emptyList(),
    val highlights: List<String> = emptyList()
)