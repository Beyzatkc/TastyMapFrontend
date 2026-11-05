package org.beem.tastymap.core.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtils {
    private const val EARTH_RADIUS = 6371000.0 // Metre cinsinden dünya yarıçapı

    // İki koordinat arası kuş uçuşu mesafe (Haversine formülü)
    fun distanceInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = (lat2 - lat1) * (PI / 180.0)
        val dLon = (lon2 - lon1) * (PI / 180.0)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * (PI / 180.0)) * cos(lat2 * (PI / 180.0)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    // Bir noktanın (P) bir doğru parçasına (A - B) olan en kısa dik mesafesi
    fun distanceToSegment(
        pLat: Double, pLng: Double,
        aLat: Double, aLng: Double,
        bLat: Double, bLng: Double
    ): Double {
        val dx = bLng - aLng
        val dy = bLat - aLat

        val l2 = dx * dx + dy * dy
        if (l2 == 0.0) return distanceInMeters(pLat, pLng, aLat, aLng)

        // İzdüşüm katsayısı (0 ile 1 arasına sınırla)
        val t = (((pLng - aLng) * dx + (pLat - aLat) * dy) / l2).coerceIn(0.0, 1.0)

        val projLat = aLat + t * dy
        val projLng = aLng + t * dx

        return distanceInMeters(pLat, pLng, projLat, projLng)
    }

    // Kullanıcının tüm rota çizgisine (segmentlerine) olan minimum mesafesi
    fun minDistanceToRoute(userLat: Double, userLng: Double, routeCoordinates: List<List<Double>>): Double {
        if (routeCoordinates.size < 2) return Double.MAX_VALUE
        var minDistance = Double.MAX_VALUE

        for (i in 0 until routeCoordinates.size - 1) {
            val p1 = routeCoordinates[i]     // [lng, lat]
            val p2 = routeCoordinates[i + 1] // [lng, lat]

            val dist = distanceToSegment(
                pLat = userLat, pLng = userLng,
                aLat = p1[1], aLng = p1[0],
                bLat = p2[1], bLng = p2[0]
            )
            if (dist < minDistance) {
                minDistance = dist
            }
        }
        return minDistance
    }
}