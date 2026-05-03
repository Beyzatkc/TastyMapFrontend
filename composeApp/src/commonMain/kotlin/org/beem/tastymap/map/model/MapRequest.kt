package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class MapRequest(
    val lat: Double,
    val lng: Double,
    val radius: Int,
    val keywords: List<String>
)
