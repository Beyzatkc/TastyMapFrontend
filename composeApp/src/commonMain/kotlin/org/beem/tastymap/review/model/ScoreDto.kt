package org.beem.tastymap.review.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreDto(
    @SerialName("type")
    val type: ScoreType,

    @SerialName("score")
    val score: Double
)