package org.beem.tastymap.data.model

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val isAvailable: Boolean = false,
    val bearing: Float = 0f
)
