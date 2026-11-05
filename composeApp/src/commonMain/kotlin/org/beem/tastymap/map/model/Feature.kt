package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class Feature(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)