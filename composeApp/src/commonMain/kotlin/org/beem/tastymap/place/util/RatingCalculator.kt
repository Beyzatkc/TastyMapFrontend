package org.beem.tastymap.place.util

import org.beem.tastymap.place.model.details.PlaceStatsResult
import org.beem.tastymap.place.model.details.ScoreMetricItem
import org.beem.tastymap.review.model.ScoreDto
import org.beem.tastymap.review.model.ScoreType
import kotlin.math.round
import kotlin.math.roundToInt

object RatingCalculator {

    fun onReviewAdded(currentRating: Double, currentCount: Int, newScore: Double): Pair<Double, Int> {
        val newCount = currentCount + 1
        val rawRating = ((currentRating * currentCount) + newScore) / newCount
        val formattedRating = round(rawRating * 10.0) / 10.0
        return Pair(formattedRating, newCount)
    }

    fun onReviewUpdated(currentRating: Double, currentCount: Int, oldScore: Double, newScore: Double): Double {
        if (currentCount <= 0) return newScore
        val rawRating = ((currentRating * currentCount) - oldScore + newScore) / currentCount
        return round(rawRating * 10.0) / 10.0
    }

    fun onReviewDeleted(currentRating: Double, currentCount: Int, deletedScore: Double): Pair<Double, Int> {
        val newCount = (currentCount - 1).coerceAtLeast(0)
        if (newCount == 0) return Pair(0.0, 0)
        val rawRating = ((currentRating * currentCount) - deletedScore) / newCount
        val formattedRating = round(rawRating * 10.0) / 10.0
        return Pair(formattedRating, newCount)
    }


    fun onStatsAdded(
        currentStats: PlaceStatsResult?,
        newRating: Double,
        newScores: List<ScoreDto>
    ): PlaceStatsResult {
        val stats = currentStats ?: PlaceStatsResult()
        val (updatedOverall, newTotalCount) = onReviewAdded(
            currentRating = stats.overallRating,
            currentCount = stats.totalReviewCount,
            newScore = newRating
        )

        // 1. Yıldız Dağılımını Güncelle
        val starKey = newRating.toInt().coerceIn(1, 5)
        val updatedDistribution = stats.starDistribution.toMutableMap()
        updatedDistribution[starKey] = (updatedDistribution[starKey] ?: 0) + 1

        // 2. Kriter Metriklerini Güncelle
        val updatedMetrics = calculateMetricsOnAdd(stats.criteriaMetrics, newScores)

        return stats.copy(
            overallRating = updatedOverall,
            totalReviewCount = newTotalCount,
            starDistribution = updatedDistribution,
            criteriaMetrics = updatedMetrics,
            highlights = extractHighlights(updatedMetrics)
        )
    }

    fun onStatsUpdated(
        currentStats: PlaceStatsResult?,
        oldRating: Double,
        newRating: Double,
        oldScores: List<ScoreDto>,
        newScores: List<ScoreDto>
    ): PlaceStatsResult {
        val stats = currentStats ?: PlaceStatsResult()
        val updatedOverall = onReviewUpdated(
            currentRating = stats.overallRating,
            currentCount = stats.totalReviewCount,
            oldScore = oldRating,
            newScore = newRating
        )

        // 1. Yıldız Dağılımı Düzeltme
        val oldStar = oldRating.toInt().coerceIn(1, 5)
        val newStar = newRating.toInt().coerceIn(1, 5)
        val updatedDistribution = stats.starDistribution.toMutableMap()
        if (oldStar != newStar) {
            updatedDistribution[oldStar] = ((updatedDistribution[oldStar] ?: 1) - 1).coerceAtLeast(0)
            updatedDistribution[newStar] = (updatedDistribution[newStar] ?: 0) + 1
        }

        // 2. Kriterleri Düzeltme
        val updatedMetrics = calculateMetricsOnUpdate(stats.criteriaMetrics, oldScores, newScores)

        return stats.copy(
            overallRating = updatedOverall,
            starDistribution = updatedDistribution,
            criteriaMetrics = updatedMetrics,
            highlights = extractHighlights(updatedMetrics)
        )
    }

    fun onStatsDeleted(
        currentStats: PlaceStatsResult?,
        deletedRating: Double,
        deletedScores: List<ScoreDto>
    ): PlaceStatsResult {
        val stats = currentStats ?: return PlaceStatsResult()
        val (updatedOverall, newTotalCount) = onReviewDeleted(
            currentRating = stats.overallRating,
            currentCount = stats.totalReviewCount,
            deletedScore = deletedRating
        )

        if (newTotalCount == 0) {
            return PlaceStatsResult()
        }

        // 1. Yıldız Dağılımı Eksiltme
        val starKey = deletedRating.toInt().coerceIn(1, 5)
        val updatedDistribution = stats.starDistribution.toMutableMap()
        updatedDistribution[starKey] = ((updatedDistribution[starKey] ?: 1) - 1).coerceAtLeast(0)

        // 2. Kriterleri Eksiltme
        val updatedMetrics = calculateMetricsOnDelete(stats.criteriaMetrics, deletedScores)

        return stats.copy(
            overallRating = updatedOverall,
            totalReviewCount = newTotalCount,
            starDistribution = updatedDistribution,
            criteriaMetrics = updatedMetrics,
            highlights = extractHighlights(updatedMetrics)
        )
    }

    private fun calculateMetricsOnAdd(
        currentMetrics: List<ScoreMetricItem>,
        newScores: List<ScoreDto>
    ): List<ScoreMetricItem> {
        val metricMap = currentMetrics.associateBy { it.type }.toMutableMap()

        newScores.filter { it.type != ScoreType.OVERALL }.forEach { scoreItem ->
            val existing = metricMap[scoreItem.type]
            if (existing != null) {
                val newCount = existing.count + 1
                val newAvg = ((existing.averageScore * existing.count) + scoreItem.score) / newCount
                metricMap[scoreItem.type] = existing.copy(
                    averageScore = roundToOneDecimal(newAvg),
                    count = newCount
                )
            } else {
                metricMap[scoreItem.type] = ScoreMetricItem(
                    type = scoreItem.type,
                    label = scoreItem.type.name,
                    averageScore = roundToOneDecimal(scoreItem.score),
                    count = 1
                )
            }
        }
        return metricMap.values.toList()
    }

    private fun calculateMetricsOnUpdate(
        currentMetrics: List<ScoreMetricItem>,
        oldScores: List<ScoreDto>,
        newScores: List<ScoreDto>
    ): List<ScoreMetricItem> {
        val metricMap = currentMetrics.associateBy { it.type }.toMutableMap()
        val oldScoresMap = oldScores.associateBy { it.type }

        newScores.filter { it.type != ScoreType.OVERALL }.forEach { newScoreItem ->
            val existing = metricMap[newScoreItem.type]
            val oldScoreVal = oldScoresMap[newScoreItem.type]?.score ?: newScoreItem.score

            if (existing != null) {
                val newAvg = ((existing.averageScore * existing.count) - oldScoreVal + newScoreItem.score) / existing.count
                metricMap[newScoreItem.type] = existing.copy(
                    averageScore = roundToOneDecimal(newAvg)
                )
            }
        }
        return metricMap.values.toList()
    }

    private fun calculateMetricsOnDelete(
        currentMetrics: List<ScoreMetricItem>,
        deletedScores: List<ScoreDto>
    ): List<ScoreMetricItem> {
        val metricMap = currentMetrics.associateBy { it.type }.toMutableMap()

        deletedScores.filter { it.type != ScoreType.OVERALL }.forEach { item ->
            val existing = metricMap[item.type]
            if (existing != null) {
                val newCount = existing.count - 1
                if (newCount <= 0) {
                    metricMap.remove(item.type)
                } else {
                    val newAvg = ((existing.averageScore * existing.count) - item.score) / newCount
                    metricMap[item.type] = existing.copy(
                        averageScore = roundToOneDecimal(newAvg),
                        count = newCount
                    )
                }
            }
        }
        return metricMap.values.toList()
    }

    private fun roundToOneDecimal(value: Double): Double {
        return (value * 10.0).roundToInt() / 10.0
    }

    private fun extractHighlights(metrics: List<ScoreMetricItem>): List<String> {
        return metrics
            .filter { it.averageScore >= 4.5 && it.count >= 1 }
            .map { "${it.type.name} Öne Çıkıyor" }
    }
}