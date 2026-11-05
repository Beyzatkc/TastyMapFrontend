package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class MapResponse(
    val status: String,
    val results: List<PlaceResult>,
    val geoJson: GeoJson
)