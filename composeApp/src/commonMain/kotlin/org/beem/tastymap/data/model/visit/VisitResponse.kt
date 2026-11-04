package org.beem.tastymap.data.model.visit

import kotlinx.serialization.Serializable

interface BaseVisitRequest {
    val placeId: String
    val placeName: String
    val categories: String?
    val city: String?
    val district: String?
    val neighbourhood: String?
    val latitude: Double
    val longitude: Double
    val averagePoint: Double?
    val isWantToPost: Boolean
}

@Serializable
data class VisitRequest(
    override val placeId: String,
    override val placeName: String,
    override val categories: String? = null,
    override val city: String? = null,
    override val district: String? = null,
    override val neighbourhood: String? = null,
    override val latitude: Double,
    override val longitude: Double,
    override val averagePoint: Double? = null,
    override val isWantToPost: Boolean = false
) : BaseVisitRequest

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