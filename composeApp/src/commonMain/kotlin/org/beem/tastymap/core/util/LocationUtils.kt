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

    /**
     * İki koordinat arasındaki mesafeyi metre cinsinden hesaplar (Harita hesaplamaları için).
     */
    fun calculateDistanceInMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // Metre cinsinden Dünya yarıçapı
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()

        val a = sin(dLat / 2).pow(2) +
                cos(lat1.toRadians()) * cos(lat2.toRadians()) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    /**
     * İki koordinat arasındaki mesafeyi UI için formatlar (Örn: "350 m" veya "2.4 km").
     */
    fun formatDistance(
        userLat: Double?,
        userLng: Double?,
        placeLat: Double?,
        placeLng: Double?
    ): String? {
        if (userLat == null || userLng == null || placeLat == null || placeLng == null) return null

        val earthRadius = 6371.0 // Kilometre cinsinden Dünya yarıçapı
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