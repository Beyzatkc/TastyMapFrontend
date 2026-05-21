package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoJson(
    val type: String,
    val features: List<Feature>
)