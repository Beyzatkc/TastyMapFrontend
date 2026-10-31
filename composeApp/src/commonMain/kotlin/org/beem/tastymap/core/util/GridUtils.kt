package org.beem.tastymap.core.util

import kotlin.math.floor
import kotlin.math.round

object GridUtils {
    const val GRID_SIZE_DEG = 0.0045 // ~500 metre

    /**
     * Koordinatı ait olduğu gridin merkez noktasına odaklar.
     */
    fun roundToGridCenter(coordinate: Double): Double {
        val base = floor(coordinate / GRID_SIZE_DEG) * GRID_SIZE_DEG
        val center = base + (GRID_SIZE_DEG / 2.0)
        return normalize(center)
    }

    fun buildGridCacheKey(gridKey: String, keywords: List<String>): String {
        val sortedTypes = keywords.sorted().joinToString("_")
        return if (sortedTypes.isNotBlank()) "$gridKey:t:$sortedTypes" else gridKey
    }

    /**
     * 4 ondalık basamağa güvenli şekilde yuvarlar (Floating-point precision fix).
     */
    fun normalize(value: Double): Double {
        return round(value * 10000.0) / 10000.0
    }

    /**
     * Saf Kotlin ile her platformda (Android, iOS, Desktop) tam 4 basamaklı nokta formatı üretir.
     * Örn: 37.95 -> "37.9500", 37.8697 -> "37.8697"
     */
    fun formatCoordinate(value: Double): String {
        val normalized = normalize(value)
        val str = normalized.toString().replace(',', '.')
        val parts = str.split('.')
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else ""
        val paddedDecimal = decimalPart.padEnd(4, '0').take(4)
        return "$integerPart.$paddedDecimal"
    }

    /**
     * Grid için standart String anahtar üretir: "37.9507:32.5057"
     */
    fun getGridKey(lat: Double, lng: Double): String {
        val centerLat = roundToGridCenter(lat)
        val centerLng = roundToGridCenter(lng)
        return "${formatCoordinate(centerLat)}:${formatCoordinate(centerLng)}"
    }

    /**
     * Merkez koordinatın etrafındaki 3x3 komşu grid anahtarlarını döner.
     */
    fun getSurroundingGridKeys(lat: Double, lng: Double, radiusMeters: Int = 500): List<String> {
        val gridRange = 1 // 3x3 matris
        val gridKeys = mutableListOf<String>()

        for (i in -gridRange..gridRange) {
            for (j in -gridRange..gridRange) {
                val offsetLat = lat + (i * GRID_SIZE_DEG)
                val offsetLng = lng + (j * GRID_SIZE_DEG)
                gridKeys.add(getGridKey(offsetLat, offsetLng))
            }
        }
        return gridKeys.distinct()
    }
}