package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HydratedPlaceCardDto(
    @SerialName("dbId") val dbId: Long? = null,
    @SerialName("placeId") val placeId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("vicinity") val vicinity: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("googleRating") val googleRating: Double? = null,
    @SerialName("tastyMapRating") val tastyMapRating: Double? = null,
    @SerialName("priceLevel") val priceLevel: Int? = null,
    @SerialName("isOpenNow") val isOpenNow: Boolean? = null,
    @SerialName("businessStatus") val businessStatus: String? = null,
    @SerialName("highlightedFoods") val highlightedFoods: List<String> = emptyList(),
    @SerialName("isAvailable") val isAvailable: Boolean = true
)