package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MapResponse(
    val status: String,
    val results: List<PlaceResult>,
    val geoJson: JsonObject
)