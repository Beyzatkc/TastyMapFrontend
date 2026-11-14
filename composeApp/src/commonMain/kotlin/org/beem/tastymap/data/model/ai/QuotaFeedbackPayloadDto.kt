package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuotaFeedbackPayloadDto(
    @SerialName("tokensUsedThisRequest") val tokensUsedThisRequest: Int = 0,
    @SerialName("tokensUsedToday") val tokensUsedToday: Int = 0,
    @SerialName("dailyTokenLimit") val dailyTokenLimit: Int = 50_000,
    @SerialName("remainingPercentage") val remainingPercentage: Double = 100.0,
    @SerialName("costTry") val costTry: Double = 0.0
)