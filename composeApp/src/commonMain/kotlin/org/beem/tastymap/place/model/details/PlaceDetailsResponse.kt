package org.beem.tastymap.place.model.details

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsResponse(
    val status: String,
    val result: PlaceDetailsResult? = null
)