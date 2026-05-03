package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationPoint(
    val lat: Double,
    val lng: Double
)