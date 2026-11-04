package org.beem.tastymap.core.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object LocationUtils {

    // KMP uyumlu radyan çevirici
    private fun Double.toRadians(): Double = this * (PI / 180.0)

    fun formatDistance(
        userLat: Double?,
        userLng: Double?,
        placeLat: Double?,
        placeLng: Double?
    ): String? {
        if (userLat == null || userLng == null || placeLat == null || placeLng == null) return null

        val earthRadius = 6371.0 // Kilometre cinsinden dünya yarıçapı
        val dLat = (placeLat - userLat).toRadians()
        val dLng = (placeLng - userLng).toRadians()

        val a = sin(dLat / 2).pow(2) +
                cos(userLat.toRadians()) * cos(placeLat.toRadians()) *
                sin(dLng / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceKm = earthRadius * c

        return if (distanceKm < 1.0) {
            "${(distanceKm * 1000).roundToInt()} m"
        } else {
            "${(distanceKm * 10.0).roundToInt() / 10.0} km"
        }
    }
}