package org.beem.tastymap.data.model.visit

import kotlinx.serialization.Serializable

@Serializable
data class VisitRequest(
    val placeId: String,
    val placeName: String,

    val categories: String? = null,
    val city: String? = null,
    val district: String? = null,
    val neighbourhood: String? = null,

    val latitude: Double,
    val longitude: Double,

    val averagePoint: Double? = null,
    val isWantToPost: Boolean = false
)

@Serializable
data class VisitResponse(
    val visitId: Long,
    val createdAt: String,
    val placeId: String,
    val placeName: String,
    val categories: String? = null,
    val city: String? = null,
    val district: String? = null,
    val neighbourhood: String? = null,
    val latitude: Double,
    val longitude: Double,
    val averagePoint: Double
)