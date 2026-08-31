package org.beem.tastymap.route.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteDirectionDto(
    @SerialName("distanceMeters")
    val distanceMeters: Double = 0.0,
    @SerialName("durationSeconds")
    val durationSeconds: Double = 0.0,
    @SerialName("formattedDistance")
    val formattedDistance: String = "0 m",
    @SerialName("formattedDuration")
    val formattedDuration: String = "0 dk",
    @SerialName("coordinates")
    val coordinates: List<List<Double>> = emptyList()
)