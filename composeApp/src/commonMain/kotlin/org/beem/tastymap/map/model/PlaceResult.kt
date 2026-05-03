package org.beem.tastymap.map.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaceResult(
    val place_id: String,
    val name: String,
    val rating: Double? = null,
    val vicinity: String? = null,
    val business_status: String? = null,
    val types: List<String>? = null,
    val geometry: GeometryContainer? = null,
    val photos: List<PhotoResult>? = null
)