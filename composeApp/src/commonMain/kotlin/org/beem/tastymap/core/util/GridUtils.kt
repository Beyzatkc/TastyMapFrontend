package org.beem.tastymap.core.util

import kotlin.math.floor

object GridUtils {
    const val GRID_SIZE_DEG = 0.0045 // ~500 metre

    /**
     * Koordinatı grid hücresinin başlangıç tabanına yuvarlar (Floor).
     */
    fun roundToGrid(coordinate: Double): Double {
        val grid = floor(coordinate / GRID_SIZE_DEG) * GRID_SIZE_DEG
        return normalize(grid)
    }

    /**
     * Koordinatı ait olduğu gridin merkez noktasına odaklar.
     */
    fun roundToGridCenter(coordinate: Double): Double {
        val base = floor(coordinate / GRID_SIZE_DEG) * GRID_SIZE_DEG
        val center = base + (GRID_SIZE_DEG / 2.0)
        return normalize(center)
    }

    /**
     * 4 ondalık basamağa normalize eder.
     */
    fun normalize(value: Double): Double {
        return (floor(value * 10000.0)) / 10000.0
    }

    /**
     * Grid için benzersiz String anahtar üretir: "37.9507:32.5057"
     */
    fun getGridKey(lat: Double, lng: Double): String {
        val centerLat = roundToGridCenter(lat)
        val centerLng = roundToGridCenter(lng)
        return "$centerLat:$centerLng"
    }

    /**
     * Merkez koordinatın etrafındaki 3x3 (veya verilen menzile göre) komşu grid anahtarlarını döner.
     */
    fun getSurroundingGridKeys(lat: Double, lng: Double, radiusMeters: Int = 500): List<String> {
        val gridRange = 1 // 500m için 3x3 matris (1 komşuluk)
        val baseCenterLat = roundToGridCenter(lat)
        val baseCenterLng = roundToGridCenter(lng)

        val gridKeys = mutableListOf<String>()

        for (i in -gridRange..gridRange) {
            for (j in -gridRange..gridRange) {
                val cellLat = normalize(baseCenterLat + (i * GRID_SIZE_DEG))
                val cellLng = normalize(baseCenterLng + (j * GRID_SIZE_DEG))
                gridKeys.add("$cellLat:$cellLng")
            }
        }
        return gridKeys
    }
}