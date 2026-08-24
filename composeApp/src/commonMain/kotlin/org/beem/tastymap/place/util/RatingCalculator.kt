package org.beem.tastymap.place.util

import kotlin.math.round

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
}