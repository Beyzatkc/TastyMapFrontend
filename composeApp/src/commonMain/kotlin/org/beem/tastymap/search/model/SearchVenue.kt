package org.beem.tastymap.search.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchVenue(
    val id: Long,
    val placeId: String,
    val name: String,
    val vicinity: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val googleRating: Double? = null,
    val tastyMapRating: Double? = null
)