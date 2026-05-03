package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class GeometryContainer(
    val location: LocationPoint
)