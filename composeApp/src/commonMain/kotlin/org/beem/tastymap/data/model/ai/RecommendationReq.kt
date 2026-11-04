package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationReq(
    @SerialName("sessionId") val sessionId: String?,
    @SerialName("query") val query: String,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("radiusKm") val radiusKm: Double? = 5.0,
    @SerialName("ignoreHealthProfile") val ignoreHealthProfile: Boolean = false,
    @SerialName("provider") val provider: String? = null
)