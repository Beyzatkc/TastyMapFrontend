package org.beem.tastymap.data.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class QuotaFeedbackPayloadDto(
    @SerialName("userId") val userId: Long? = null,
    @SerialName("remainingQuotaPercentage") val remainingQuotaPercentage: Double = 100.0,
    @SerialName("totalPromptTokens") val totalPromptTokens: Int = 0,
    @SerialName("totalCompletionTokens") val totalCompletionTokens: Int = 0
)